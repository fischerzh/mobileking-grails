package ch.freebo

import org.springframework.dao.DataIntegrityViolationException
import static java.util.UUID.randomUUID;

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
	
	def callGCMService()
	{
		println params
		addMessages("MESSAGE", params.inputMessage)
		sendMessage();
	}
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def callGCMServiceMsg(User user)
	{
		this.user = user
		println "this.user: " + this.user
		println params
		if(user.isNotificationEnabled)
			sendMessage();
	}
	
	def addMessages(String type, String message)
	{
		messageList[type] = message
	}
	
	def deleteMessages()
	{
		messageList = [:]
	}
	
	
	def sendMessage = {
		def deviceList
		if(this.user)
		{
			def user = User.find(this.user)
			deviceList = user.devices
		}
		else
		{
			deviceList = Devices.all
		}
		def idList = []
		deviceList.each { 
			println it.deviceId
			idList.add(it.deviceId)
		}
		println "Device Ids: " + idList
//		println "Device List: " +deviceList
		
		['deviceToken', 'messageKey', 'messageValue'].each {
				key -> params[key] = [idList].flatten().findAll { it }
		}
		
//		def messageList = [:]
		
//		messageList['STATUS'] = 'Neuer Status!'
//		messageList['BADGE'] = 'Neuer Badge!'
//		messageList['RANG'] = 'Neuer Rang!'
		
		println "messageList: " +messageList
 		
		def messages = params.messageKey.inject([:]) {
				currentMessages, currentKey ->
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
					def newLogMessage = new LogMessages(messageId: uuid, action: "NotificationSent", createDate: date.toString(), logDate: date, message: it.toString()).save(failOnError:true)
					user.addToLogMessages(newLogMessage)
				}
				user.save(failOnError:true)
			}
			else
			{
				flash.message = message(code: 'default.created.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel Message verschickt: '), params.inputMessage])
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

    def create() {
		switch (request.method) {
		case 'GET':
			def retailerList = Retailer.list(params)
//			println retailerList
		
			def productListForShopping = Product.list(params)
			
        	[userList: UserRole.findAllByRole(userRole).user, retailerList: retailerList, productListForShopping: productListForShopping]
			break
		case 'POST':
			println "ControlPanel, create Shopping: " +params
			def newUserRankList = []
			
			def user = User.findById(params.userList.username)
			def retailer = Retailer.findById(params.retailerList.name)
			
			/** create a new shopping Instance with many shoppingItems **/
			def shoppingInstance = new Shopping(date: new Date(), retailer: retailer, user: user).save(failOnError:true)
			
	        if (shoppingInstance) 
			{
				
				/** create new shoppingItems and add to shoppingInstance for selected Retailer! **/
				params.product.eachWithIndex { obj, i ->
					def product = Product.findById(obj)
//					println "Products in list: " +Product.findById(obj)
					def anzahl = params.anzahl[i]
					def preis = params.preis[i]?Float.parseFloat(params.preis[i]):null
					if(anzahl && preis)
					{
						def shoppingItem = new ProductShoppings(qty: anzahl, price: preis, product: product)
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
					println "allOptIns: " +allOptInProducts
					allOptInProducts.each { Product localProd ->
						shoppingInstance.productShoppings.each {
							if(it.product == localProd)
							{
								def	OptIn userProd = OptIn.findByProductAndUserAndOptIn(localProd, user, true, [max:1, sort:"lastUpdated", order:"desc"])
								if(!userProd.isActive)
								{
									log.debug("user Opt-In " + userProd)
									println "user Opt-In: " +userProd
									println "NEW SHOPPING: Setting product to active (pre opt-in was registered)!" + localProd
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
				flash.message = message(code: 'default.deleted.message', args: [message(code: 'controlPanel.label', default: 'Einkauf konnte nicht erstellt werden!'), shoppingInstance])
	            redirect action: 'create'
				break
			}
			
			addMessages("MESSAGE", "Neuer Einkauf. Schau nach ob du einen neuen Rang erreicht hast!")
			callGCMServiceMsg(user)

						flash.message = message(code: 'default.created.message', args: [message(code: 'controlPanel.label', default: 'Neuer Einkauf erstellt!'), shoppingInstance])
			
			redirect action: 'create'
			
			return
			
		}
    }

    def show() {
        def controlPanelInstance = ControlPanel.get(params.id)
        if (!controlPanelInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel'), params.id])
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
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [controlPanelInstance: controlPanelInstance]
			break
		case 'POST':
	        def controlPanelInstance = ControlPanel.get(params.id)
	        if (!controlPanelInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (controlPanelInstance.version > version) {
	                controlPanelInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'controlPanel.label', default: 'ControlPanel')] as Object[],
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

			flash.message = message(code: 'default.updated.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel'), controlPanelInstance.id])
	        redirect action: 'show', id: controlPanelInstance.id
			break
		}
    }

    def delete() {
        def controlPanelInstance = ControlPanel.get(params.id)
        if (!controlPanelInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel'), params.id])
            redirect action: 'list'
            return
        }

        try {
            controlPanelInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
