package ch.freebo

import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import groovy.json.JsonBuilder
import groovy.grape.Grape

import org.springframework.web.multipart.commons.CommonsMultipartFile

import static java.util.UUID.randomUUID;


/** Data access from remote for App */
class DataController {

	def springSecurityService

	def DataGeneratorService dataGenerator = new DataGeneratorService()

	def RankingService rankingService = new RankingService()

	def User user

	@Secured([
		'ROLE_USER',
		'IS_AUTHENTICATED_FULLY'
	])
	def loginFromApp() {
		println "DataController: loginFromApp()"
		this.user = User.findByUsername(springSecurityService.currentUser.toString())

		if(user==null) {
			render([status: "FAILED", exception: "User nicht gefunden!"] as JSON)
		}

		//		println user.shoppings.productShoppings.collect()

		dataGenerator.setUser(user)
		rankingService.setUser(user)

		/** PREPARE, CALCULATE AND SET UP JSON DATA FOR RESPONSE*/

		def jsonExport = dataGenerator.getJSONData()

		if(!user.isActiveApp)
		{
			jsonExport.isactiveapp = false
		}
		else
		{
			jsonExport.isactiveapp = true
		}

		def json = new JsonBuilder(jsonExport)


		println json.toPrettyString()
		//return product and user settings!!
		Date date = new Date()

		if(user)
		{
			user.isActiveApp = true

			this.user = dataGenerator.registerUserAndDevice(params.regId, params.deviceType, params.deviceOs)

			if(user.save(flush: true))
				new UserLogin( user: user, loginDate: date, success: true).save(failOnError:true)
			render json
		}
		else
		{
			new UserLogin( user: user, loginDate: date, success: false).save(failOnError:true)
			render([status: "FAILED", exception: "Login fehlerhaft!"] as JSON)
		}
	}

	@Secured([
		'ROLE_USER',
		'IS_AUTHENTICATED_FULLY'
	])
	def updateFavorites()
	{
		println "DataController: updateFavorites()"
		println "Params" + params
		this.user = User.findByUsername(springSecurityService.currentUser.toString())

		def LogMessages newLogMessage
		def uuid = randomUUID() as String

		if(user==null)
		{
			render([status: "FAILED", exception: "User nicht gefunden!"] as JSON)
		}

		if(params.ean)
		{
			def prod = Product.findByEan(params.ean)
			def OptIn optAction
			def pointsForProduct

			if(!prod)
			{
				def productResponse = getHTMLPage(params.ean);
				def hersteller = Manufacturer.findByName(productResponse.producer.toString())?:new Manufacturer(name: productResponse.producer.toString()).save(failOnError:true)
				def prodSegment1 = ProductSegment.findByName("Lebensmittel / Getraenke / Tabakwaren")?:new ProductSegment(name:"Lebensmittel / Getraenke / Tabakwaren").save(flush:true)
				def prodCategory1 = ProductCategory.findByName(productResponse.category.toString())?:new ProductCategory(name:productResponse.category.toString(),productSegment:prodSegment1).save(flush:true)
				prod = new Product(name: productResponse.productname.toString(), ean: params.ean.toString(), imageLink: productResponse.productlink.toString(), productCategory :  prodCategory1, manufacturer: hersteller).save(failOnError:true)
			}

			newLogMessage = new LogMessages(messageId: uuid, action: "OptIn: "+params.optin, createDate: new Date().toString(), logDate: new Date(), message: "Product: "+ prod).save(failOnError:true)
			user.addToLogMessages(newLogMessage).save(failOnError:true)


			if(prod!=null)
			{
				pointsForProduct = rankingService.calculatePointsForProduct(prod, user)

				log.debug "Found product for Opt-In: " + prod.name
				log.debug "Param Opt-In: " +params.optin
				log.debug "Param Opt-In: " +params.optin

				if(pointsForProduct > 0)
				{
					//OPT IN
					if(params.optin)
					{
						if(!rankingService.hasUserOptIn(prod, user))
						{
							optAction = new OptIn(user: user, product: prod, optIn: true, isActive:true)
							println "Opt-in: " +optAction
							if(!optAction.save(failOnError:true))
							{
								render([status: "FAILED", exception: "Opt-In fehlerhaft: Aktion nicht gespeichert!"] as JSON)
							}
							else
							{
								rankingService.calculateRankingForOptAction(prod, user)

								render([status: "SUCCESS", exception: "Opt In erfolgreich!"] as JSON)
							}
						}
						render([status: "SUCCESS", exception: "Opt In schon aktiv!"] as JSON)


					}
					//OPT OUT
					else if(params.optout)
					{
						if(rankingService.hasUserOptIn(prod, user))
						{
							optAction = new OptIn(user: user, product: prod, optIn: false, isActive: false )
							println "Opt-Out: " +optAction
							if(!optAction.save(failOnError:true))
							{
								render([status: "FAILED", exception: "Opt-Out fehlerhaft: Aktion nicht gespeichert!"] as JSON)
							}
							else
							{
								rankingService.calculateRankingForOptAction(prod, user)

								render([status: "SUCCESS", exception: "Opt Out erfolgreich!"] as JSON)
							}
						}
						render([status: "SUCCESS", exception: "Opt Out schon erfolgt!"] as JSON)

					}
				}
				else if(pointsForProduct == 0)
				{
					if(params.optin)
					{
						if(!rankingService.hasUserOptIn(prod, user))
						{
							optAction = new OptIn(user: user, product: prod, optIn: true, isActive: false).save(failOnError:true)
							render([status: "SUCCESS", exception: "Noch keine Einkäufe vorhanden!"] as JSON )
						}
						else
						{
							render([status: "SUCCESS", exception: "Opt In schon aktiv!"] as JSON)
						}
					}
					else if(params.optout)
					{
						optAction = new OptIn(user: user, product: prod, optIn: false).save(failOnError:true)
						render([status: "SUCCESS", exception: "Opt-Out erfolgreich!"] as JSON)

					}
				}
			}
			else
			{

				println "Product not found for Opt-In!"
				render([status: "FAILED", exception: "Opt In/Out fehlerhaft: Produkt nicht gefunden!"] as JSON)
			}

		}

	}

	def getHTMLPage(String ean)
	{
		println "EAN search in Codecheck: " + ean

		String ENCODING = "UTF-8"

		@Grab(group='org.ccil.cowan.tagsoup', module='tagsoup', version='1.2' )
				def tagsoupParser = new org.ccil.cowan.tagsoup.Parser()
		def slurper = new XmlSlurper(tagsoupParser)
		def url = "http://www.codecheck.info/product.search?q="+ean+"&OK=Suchen"
		def htmlParseCodeCheck

		new URL(url).withReader ('UTF-8') { reader ->

			htmlParseCodeCheck =  slurper.parse("http://www.codecheck.info/product.search?q="+ean+"&OK=Suchen")
		}

		def productName
		def producer
		def category
		def size
		def productLink = "http://www.codecheck.info"

		htmlParseCodeCheck.'**'.findAll{ it.@class == 'h1Title'}.each {
			println it
			productName = it
		}
		htmlParseCodeCheck.'**'.findAll{it.@class == 'htit'}.each { node ->
			println "htit elements: " + node.text()
			def parentNode = node.text().replace("\n", "").replace("\r", "")
			if(node.text().trim().contains("Hersteller") && !node.text().trim().contains("Strichcodeanmelder"))
			{

				println "node.parent: " +node.parent().text()
				println "node.parent.span: " +node.parent().span.text()
				def split = node.parent().text().split('br')
				println "node.parent.br: " +split
				node.parent().each {
					producer = it.text().replace("\n", "").replace("\r", "").replace(parentNode, " ").trim().replace("Vertrieb", "").trim()
					println "Hersteller: " +producer
				}

			}

			if(node.text().contains("Menge"))
			{
				println "parent: " +node.parent().text()

			}

		}


		htmlParseCodeCheck.'**'.find { it['@id'] == 'ExternalLink_1' }.each {
			println "externalLink: " +it
			category = it.text().trim()
		}

		htmlParseCodeCheck.'**'.findAll{
			it.name().contains('img')
		}.each {
			if( it.@src.text().contains("/img/"))
			{
				println it.@src.text()
				productLink=productLink+it.@src.text()

			}

		}

		println "ProductLink for product " +productName + " " + productLink
		def productResponse = [:]
		productResponse.productname = productName
		productResponse.productlink = productLink
		productResponse.producer = producer
		productResponse.category = category
		println productResponse
		return productResponse

	}

	@Secured([
		'ROLE_USER',
		'IS_AUTHENTICATED_FULLY'
	])
	def updateUserSettings()
	{
		println "DataController, UpdatUserSettings: " +params
		def user = User.findByUsername(springSecurityService.currentUser.toString())

		if(params.updateFromApp && user)
		{

			user.properties = params

			println "user properties: " + user.properties

			if (!user.save(flush: true)) {

				def errorMessage
				user.errors.allErrors.each {
					println it
					errorMessage += it.toString().toLowerCase().contains("ch.freebo.user.username.unique.error")?"Username not uniqe":" "
					errorMessage += it.toString().toLowerCase().contains("ch.freebo.user.email.unique.error")?"E-Mail schon vorhanden":" "
				}
				println errorMessage

				render([status: "FAILED", exception: "Aktualisierung fehlgeschlagen! "+errorMessage] as JSON)

			}

			render([status: 'Success', exception: 'Aktualisierung erfolgreich!'] as JSON)

		}
	}

	@Secured([
		'ROLE_USER',
		'IS_AUTHENTICATED_FULLY'
	])
	def updateUserLog()
	{
		println "DataController, UpdateUserLog:" + params
		def user = User.findByUsername(springSecurityService.currentUser.toString())

		def msgId = params.logMessageId
		if(msgId=='null' || msgId==null)
		{
			println "Msg Id is Null!!"
			def uuid = randomUUID() as String
			msgId = uuid
		}

		def newLogMessage = new LogMessages(messageId: msgId, action: params.title, createDate: params.createDate, logDate: new Date(), message: params.message)
		if(newLogMessage.save(failOnError:true))
		{
			user.addToLogMessages(newLogMessage)
		}
		else
		{
			render([status: "FAILED", exception: params.logMessageId] as JSON)
		}

		if(!user.save(failOnError:true))
		{
			println "FAILED: " +params
			render([status: "FAILED", exception: params.logMessageId] as JSON)
		}
		println "SUCCESS: "+params
		render([status: "SUCCESS", exception: params.logMessageId] as JSON)
	}

	@Secured([
		'ROLE_USER',
		'IS_AUTHENTICATED_FULLY'
	])
	def updateUserFiles()
	{
		println "DataController, updateUserFiles:" + params
		def user = User.findByUsername(springSecurityService.currentUser.toString())

		//		byte[] inputByteArray = params.image

		CommonsMultipartFile file = request.getFile('uploadFile')

		def webRootDir = servletContext.getRealPath("/WEB-INF")

		def userDir = new File(webRootDir, "/uploads/"+user+"/salesSlips/")
		userDir.mkdirs()
		file.transferTo( new File( userDir,file.originalFilename))

	}

	def updateErrorLogs()
	{
		println "DataController, updateErrorLogs:" + params

		//		byte[] inputByteArray = params.image

		CommonsMultipartFile file = request.getFile('uploadFile')

		def webRootDir = servletContext.getRealPath("/WEB-INF")

		def userDir = new File(webRootDir, "/logs/")
		userDir.mkdirs()
		file.transferTo( new File( userDir,file.originalFilename))

	}

}
