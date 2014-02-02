package ch.freebo

import java.text.SimpleDateFormat;

import org.springframework.dao.DataIntegrityViolationException

import static java.util.UUID.randomUUID;
import grails.converters.JSON
import grails.plugins.springsecurity.Secured


@Secured(['ROLE_ADMIN'])
class ControlPanelController {

	static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

	def androidGcmService
	def RankingService rankingService = new RankingService()

	def DataGeneratorService dataGenerator = new DataGeneratorService()

	def messageList = [:]

	def userRole = Role.findByAuthority('ROLE_USER')


	def User user

	def callGCMService() {
		println params
		if(params.controlPanelUser)
			this.user = User.findByUsername(params.controlPanelUser)
		addMessages("MESSAGE", params.inputMessage)
		sendMessage();
	}

	@Secured([
		'ROLE_USER',
		'IS_AUTHENTICATED_FULLY'
	])
	def callGCMServiceMsg(User user) {
		this.user = user
		println "this.user: " + this.user
		println params
		if(user.isNotificationEnabled)
			sendMessage();
	}

	def addMessages(String type, String message) {
		messageList[type] = message
	}

	def deleteMessages() {
		messageList = [:]
	}


	def sendMessage = {
		def deviceList
		if(this.user) {
			def user = User.find(this.user)
			deviceList = user.devices
		}
		else {
			deviceList = Devices.all
		}
		def idList = []
		deviceList.each {
			println it.deviceId
			idList.add(it.deviceId)
		}
		println "Device Ids: " + idList
		//		println "Device List: " +deviceList

		[
			'deviceToken',
			'messageKey',
			'messageValue'
		].each { key ->
			params[key] = [idList].flatten().findAll { it }
		}

		//		def messageList = [:]

		//		messageList['STATUS'] = 'Neuer Status!'
		//		messageList['BADGE'] = 'Neuer Badge!'
		//		messageList['RANG'] = 'Neuer Rang!'

		println "messageList: " +messageList

		def messages = params.messageKey.inject([:]) { currentMessages, currentKey ->
			currentMessages << messageList//[ "1" : params.inputMessage]
		}

		println "messages: " + messages

		if(idList)
		{
			androidGcmService.sendMessage(messages, params.deviceToken,"", grailsApplication.config.android.gcm.api.key).toString()

			def date = new Date()
			def uuid = randomUUID() as String
			if(user)
			{
				messages.each {
					def newLogMessage = new LogMessages(user: user, messageId: uuid, userAction: "NotificationSent", createDate: date.toString(), logDate: date, message: it.toString()).save(failOnError:true)
				}
				user.save(failOnError:true)
			}
			else
			{
				flash.message = message(code: 'default.created.message', args: [
					message(code: 'controlPanel.label', default: 'ControlPanel Message verschickt: '),
					params.inputMessage
				])
			}
		}

		if(this.user)
			return
		else
			redirect controller: 'controlPanel', action: 'list'
	}


	def index() {
		redirect action: 'list', params: params
	}

	def list() {

		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		[controlPanelInstanceList: UserRole.findAllByRole(userRole).user, controlPanelInstanceTotal: User.count()]
	}

	def displayImage()
	{
		println "getting Image: " + params.img
		def webRootDir = servletContext.getRealPath("/WEB-INF")

		File image = new File(webRootDir, "/uploads/"+params.user+"/salesSlips/"+params.img)
		println "image: " +image
		//		File image = new File(IMAGES_DIR.getAbsoluteFilePath() + File.separator + params.img)
		if(!image.exists()) {
			response.status = 404
		} else {
			response.setContentType("application/jpg")
			OutputStream out = response.getOutputStream();
			out.write(image.bytes);
			out.close();
		}
	}

	def create() {
		switch (request.method) {
			case 'GET':
				println "controlPanel.create: GET " + params
				def retailerList = Retailer.list(params)
			//			println retailerList
				def salesSlips = []
				salesSlips = ScannedReceipt.findAll { isApproved == 1	}
				def productListForShopping
				def imageList = []

				if(params.selectedScannedReceipt)
				{
					def ScannedReceipt receipt = ScannedReceipt.findById(params.selectedScannedReceipt)
					def User user = receipt.user
					params.user = user
					productListForShopping = dataGenerator.getAllOptInProductsForUser(user)
					def webRootDir = servletContext.getRealPath("/WEB-INF")
					def item = ScannedReceipt.findById(params.selectedScannedReceipt)
					def scannedReceiptItems = ScannedReceipt.findAllByScanDate(item.scanDate)
					scannedReceiptItems.each {
						println "Image: " +it.fileName
						File imageFile = new File( webRootDir, "/uploads/"+user+"/salesSlips/"+it.fileName+"_part"+it.filePart+".png")
						imageList.add(imageFile)
					}
					println "ImageList: " + imageList
				}

				[salesSlips: salesSlips, selectedScannedReceipt: params.selectedScannedReceipt, user: params.user, retailerList: retailerList, productListForShopping: productListForShopping, imageList: imageList]
				break
			case 'POST':
				println "ControlPanel, create Shopping: " +params
				def newUserRankList = []

				def user = User.findByUsername(params.user)
				def retailer = Retailer.findById(params.retailerList.name)

				def ScannedReceipt item = ScannedReceipt.findById(params.selectedScannedReceipt)
				def scannedReceiptItems = ScannedReceipt.findAllByScanDate(item.scanDate)
				println "Found receipt items:" + scannedReceiptItems + " scanDate: " +item.scanDate

				def scanDate = Date.parse("yyyyMMddHHmms",item.fileName.replace("scan_","").trim())

				println "scanDate: " +scanDate

				def isSalesVerify = params.salesVerified=="Verify"?true:false

				if(params.product && isSalesVerify)
				{

						/** create a new shopping Instance with many shoppingItems **/
						def shoppingInstance = new Shopping(date: scanDate, retailer: retailer, user: user, receipt: item).save(failOnError:true)


						if (shoppingInstance)
						{

							/** create new shoppingItems and add to shoppingInstance for selected Retailer! **/
							if(params.product.class.isArray())
							{
								params.product.eachWithIndex { obj, i ->

									def product = Product.findById(obj)
									//					println "Products in list: " +Product.findById(obj)
									def anzahl = params.anzahl[i]
									def preis = params.preis[i]?Float.parseFloat(params.preis[i]):null
									//					def isVerified = params.isVerified[i].toBoolean()
									if(anzahl && preis)
									{
										def shoppingItem = new ProductShoppings(qty: anzahl, price: preis, product: product, isVerified: isSalesVerify)
										if(shoppingItem.save(failOnError:true))
										{
											shoppingInstance.addToProductShoppings(shoppingItem)
										}
									}

								}
							}
							else
							{

								def product = Product.findById(params.product)
								def anzahl = params.anzahl
								def preis = params.preis?Float.parseFloat(params.preis):null
								//					def isVerified = params.isVerified[i].toBoolean()
								if(anzahl && preis)
								{
									def shoppingItem = new ProductShoppings(qty: anzahl, price: preis, product: product, isVerified: isSalesVerify)
									if(shoppingItem.save(failOnError:true))
									{
										shoppingInstance.addToProductShoppings(shoppingItem)
									}
								}
							}
							if(shoppingInstance.save(failOnError:true))
							{
								// set the receipts to Approved!
								scannedReceiptItems.each { ScannedReceipt sr ->
									sr.isApproved = params.salesVerified=="Verify"?2:0
									sr.shopping = shoppingInstance
									sr.purchaseDate = params.shoppingDate
								}
								// calculate new Ranking after Shopping
								newUserRankList = rankingService.calculateRankingForShopping(user, shoppingInstance)

								// update Opt-In if user has done pre Opt-In
								def allOptInProducts = dataGenerator.getAllOptInProductsForUser(user)

								allOptInProducts.each { Product localProd ->
									shoppingInstance.productShoppings.each {
										if(it.product == localProd)
										{
											// Check if the user made a pre-opt in, if the OptIn was not active yet: set isActive = true
											def	OptIn userProd = OptIn.findByProductAndUserAndOptIn(localProd, user, true, [max:1, sort:"lastUpdated", order:"desc"])
											if(!userProd.isActive)
											{
												log.debug("user Opt-In " + userProd)
												userProd.isActive = true
												userProd.save(failOnError:true)
											}
										}
									}
								}

							}
							else
							{
								flash.message = message(code: 'default.created.message', args: [
									message(code: 'controlPanel.label', default: 'Einkauf konnte nicht erstellt werden!'),
									shoppingInstance
								])
								redirect action: 'create'
								break
							}
						}

//					else
//					{
//						// set the receipts to Rejected!
//						scannedReceiptItems.each { ScannedReceipt sr ->
//							sr.isApproved = params.salesVerified=="Verify"?2:0
//							sr.purchaseDate = params.shoppingDate
//							sr.rejectMessage = params.rejectMessage
//						}
//						if(user.isNotificationEnabled)
//						{
//							addMessages("MESSAGE", "Einkauf konnte nicht verifiziert werden!")
//							callGCMServiceMsg(user)
//							flash.message = 'Einkauf nicht verifiziert! Message an User ' +user+" geschickt!)"
//							redirect action: 'create'
//							return
//
//						}
//
//						flash.message = 'Einkauf nicht verifiziert! KEINE Message an User ' +user+ " geschickt! (Notifications disabled!)"
//						redirect action: 'create'
//						return
//
//					}
					if(user.isNotificationEnabled)
					{
						addMessages("MESSAGE", "Neuer Einkauf. Schau nach ob du einen neuen Rang erreicht hast!")
						callGCMServiceMsg(user)
						flash.message = 'Neuer Einkauf erstellt fŸr user: ' +user +' Keine Message geschickt (Notification disabled!)'

					}
					flash.message = 'Neuer Einkauf erstellt fŸr user: ' +user + 'Message geschickt!'

					redirect action: 'create'
					return

				}
				else if (!isSalesVerify)
				{
					// set the receipts to Rejected!
						scannedReceiptItems.each { ScannedReceipt sr ->
							sr.isApproved = params.salesVerified=="Verify"?2:0
							sr.purchaseDate = params.shoppingDate
							sr.rejectMessage = params.rejectMessage
						}
						if(user.isNotificationEnabled)
						{
							addMessages("MESSAGE", "Einkauf konnte nicht verifiziert werden!")
							callGCMServiceMsg(user)
							flash.message = 'Einkauf nicht verifiziert! Message an User ' +user+" geschickt!)"
							redirect action: 'create'
							return

						}

				}
				
				flash.message = 'Einkauf kann nicht gereniert werden wenn keine EinkŠufe definiert wurden (Preis, Anzahl!)'
				
				redirect action: 'create'
				return



		}
	}
	
	def createShopping()
	{
			println "ControlPanel, create Shopping: " +params
			def newUserRankList = []
			
			def user = User.findById(params.user)
			def retailer = Retailer.findById(params.retailer)
			
			/** create a new shopping Instance with many shoppingItems **/
			if(user && retailer)
			{
				def shoppingInstance = new Shopping(date: new Date(), retailer: retailer, user: user).save(failOnError:true)
				
				if (shoppingInstance)
				{
					
					/** create new shoppingItems and add to shoppingInstance for selected Retailer! **/
					if(params.product.class.isArray())
							{
								params.product.eachWithIndex { obj, i ->
									println "Obj: "+ obj + ", i: " + i
									def product = Product.findByEan(obj)
									println "Products in list: " +product
									def anzahl = params.anzahl[i]
									println "anzahl: " + anzahl
									def preis = params.preis[i]?Float.parseFloat(params.preis[i]):null
									//					def isVerified = params.isVerified[i].toBoolean()
									if(anzahl && preis)
									{
										def shoppingItem = new ProductShoppings(qty: anzahl, price: preis, product: product, isVerified: true)
										if(shoppingItem.save(failOnError:true))
										{
											shoppingInstance.addToProductShoppings(shoppingItem)
										}
									}

								}
							}
							else
							{

								def product = Product.findByEan(params.product)
								println "product: " + product
								def anzahl = params.anzahl
								println "anzahl: " + anzahl
								def preis = params.preis?Float.parseFloat(params.preis):null
								//					def isVerified = params.isVerified[i].toBoolean()
								if(anzahl && preis)
								{
									def shoppingItem = new ProductShoppings(qty: anzahl, price: preis, product: product, isVerified: true)
									if(shoppingItem.save(failOnError:true))
									{
										shoppingInstance.addToProductShoppings(shoppingItem)
									}
								}
							}
					
					if(shoppingInstance.save(failOnError:true))
					{
						// calculate new Ranking after Shopping
						newUserRankList = rankingService.calculateRankingForShopping(user, shoppingInstance)
						
						// update Opt-In if user has done pre Opt-In
						def allOptInProducts = dataGenerator.getAllOptInProductsForUser(user)
	
						allOptInProducts.each { Product localProd ->
							shoppingInstance.productShoppings.each {
								if(it.product == localProd)
								{
									// Check if the user made a pre-opt in, if the OptIn was not active yet: set isActive = true
									def	OptIn userProd = OptIn.findByProductAndUserAndOptIn(localProd, user, true, [max:1, sort:"lastUpdated", order:"desc"])
									if(!userProd.isActive)
									{
										log.debug("user Opt-In " + userProd)
										userProd.isActive = true
										userProd.save(failOnError:true)
									}
								}
							}
						}
						
					}
					/** pass the current shopping to be excluded (calculateCurrentPoints) and then included (calculateAllPoints) for the Ranking **/
						
				}
				else
				{
					render([status: "FAILED", exception: "Einkauf konnte nicht erstellt werden!"] as JSON)
					return
				}
				
				addMessages("MESSAGE", "Neuer Einkauf. Schau nach ob du einen neuen Rang erreicht hast!")
				callGCMServiceMsg(user)
	
				render([status: "SUCCESS", exception: "Neuer Einkauf erstellt! Message an User geschickt!"] as JSON)
				
				return
			}
			else
			{
				render([status: "FAILED", exception: "Einkauf konnte nicht erstellt werden!"] as JSON)
				return
			}
			
			
	}

	def show() {
		def controlPanelInstance = ControlPanel.get(params.id)
		if (!controlPanelInstance) {
			flash.message = message(code: 'default.not.found.message', args: [
				message(code: 'controlPanel.label', default: 'ControlPanel'),
				params.id
			])
			redirect action: 'list'
			return
		}

		[controlPanelInstance: controlPanelInstance]
	}

	def edit() {
		switch (request.method) {
			case 'GET':
				def controlPanelInstance = ControlPanel.get(params.id)
				if (!controlPanelInstance) {
					flash.message = message(code: 'default.not.found.message', args: [
						message(code: 'controlPanel.label', default: 'ControlPanel'),
						params.id
					])
					redirect action: 'list'
					return
				}

				[controlPanelInstance: controlPanelInstance]
				break
			case 'POST':
				def controlPanelInstance = ControlPanel.get(params.id)
				if (!controlPanelInstance) {
					flash.message = message(code: 'default.not.found.message', args: [
						message(code: 'controlPanel.label', default: 'ControlPanel'),
						params.id
					])
					redirect action: 'list'
					return
				}

				if (params.version) {
					def version = params.version.toLong()
					if (controlPanelInstance.version > version) {
						controlPanelInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
								[
									message(code: 'controlPanel.label', default: 'ControlPanel')] as Object[],
								"Another user has updated this ControlPanel while you were editing")
						render view: 'edit', model: [controlPanelInstance: controlPanelInstance]
						return
					}
				}

				controlPanelInstance.properties = params

				if (!controlPanelInstance.save(flush: true)) {
					render view: 'edit', model: [controlPanelInstance: controlPanelInstance]
					return
				}

				flash.message = message(code: 'default.updated.message', args: [
					message(code: 'controlPanel.label', default: 'ControlPanel'),
					controlPanelInstance.id
				])
				redirect action: 'show', id: controlPanelInstance.id
				break
		}
	}

	def delete() {
		def controlPanelInstance = ControlPanel.get(params.id)
		if (!controlPanelInstance) {
			flash.message = message(code: 'default.not.found.message', args: [
				message(code: 'controlPanel.label', default: 'ControlPanel'),
				params.id
			])
			redirect action: 'list'
			return
		}

		try {
			controlPanelInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [
				message(code: 'controlPanel.label', default: 'ControlPanel'),
				params.id
			])
			redirect action: 'list'
		}
		catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [
				message(code: 'controlPanel.label', default: 'ControlPanel'),
				params.id
			])
			redirect action: 'show', id: params.id
		}
	}
}
