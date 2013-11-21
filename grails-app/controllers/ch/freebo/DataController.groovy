package ch.freebo

import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import groovy.json.JsonBuilder
import static java.util.UUID.randomUUID;


/** Data access from remote for App */
class DataController {
	
	def springSecurityService
	
	def DataGeneratorService dataGenerator = new DataGeneratorService()
	
	def RankingService rankingService = new RankingService()
	
	def User user

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def loginFromApp() {
		println "DataController: loginFromApp()"
		this.user = User.findByUsername(springSecurityService.currentUser.toString())
		
		if(user==null)
		{
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
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def updateFavorites()
	{
		println "DataController: updateUserInfo()"
		println "Params" + params
		this.user = User.findByUsername(springSecurityService.currentUser.toString())
		
		if(user==null)
		{
			render([status: "FAILED", exception: "User nicht gefunden!"] as JSON)
		}
		
		if(params.ean)
		{
			def prod = Product.findByEan(params.ean)
			
			def pointsForProduct = rankingService.calculatePointsForProduct(prod, user)
			
			if(prod && pointsForProduct != 0)
			{
				println "Found product for Opt-In: " + prod.name
				//OPT IN
				if(params.optin)
				{
					def userProd = new UserProduct(user: user, product: prod, optIn: true, updated: new Date())
					println "Opt-in: " +userProd
					if(!userProd.save(failOnError:true))
					{
						render([status: "FAILED", exception: "Opt-In fehlerhaft: Produkt nicht gefunden!"] as JSON)
					}
				}
				//OPT OUT
				else if(params.optout)
				{
					def userProd = new UserProduct(user: user, product: prod, optIn: false, updated: new Date() )
					println "Opt-Out: " +userProd
					if(!userProd.save(failOnError:true))
					{
						render([status: "FAILED", exception: "Opt-Out fehlerhaft: Produkt nicht gefunden!"] as JSON)
					}
				}
				
				render([status: "SUCCESS", exception: "Opt In/Out erfolgreich: Produkt aktualisiert!"] as JSON)

			}
			else
			{
				println "Product not found for Opt-In!"
				if(pointsForProduct == 0 && prod)
					render([status: "FAILED", exception: "Opt In fehlerhaft: Noch keine Einkäufe vorhanden!"] as JSON)
				else
					render([status: "FAILED", exception: "Opt In/Out fehlerhaft: Produkt nicht gefunden!"] as JSON)
				
			}
		}
		
	}
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def updateUserInfo()
	{
		println "DataController, UpdatUserInfo: " +params
		def user = User.findByUsername(springSecurityService.currentUser.toString())
		
		
	}
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
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
	
}
