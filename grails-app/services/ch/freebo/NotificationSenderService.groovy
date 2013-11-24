package ch.freebo

class NotificationSenderService {
	
	def androidGcmService
	
	def grailsApplication = new ControlPanel().domainClass.grailsApplication
	
	String message
	User user
	
	def messageList = [:]
	

	def callGCMService()
	{
		println params
		addMessages("MESSAGE", params.inputMessage)
		sendMessage();
	}
	
	def callGCMServiceMsg(User user)
	{
		this.user = user
		println "this.user: " + this.user
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
		def params = [:]
		if(user)
		{
			def user = User.find(user)
			deviceList = user.devices
		}
		else
			deviceList = Devices.all
		def idList = []
		deviceList.each {
			println it.deviceId
			idList.add(it.deviceId)
		}
		println "Device Ids: " + idList
		println "Device List: " +deviceList
		
		['deviceToken', 'messageKey', 'messageValue'].each {
				key -> params[key] = [idList].flatten().findAll { it }
		}
		def messages = params.messageKey.inject([:]) {
				currentMessages, currentKey ->
				currentMessages << messageList
		}
		
//		flash.message = message(code: 'default.created.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel Message verschickt: '), params.inputMessage])
		
		androidGcmService.sendMessage(messages, params.deviceToken,"", grailsApplication.config.android.gcm.api.key).toString()
		if(params.user)
			return
		else
			redirect controller: 'controlPanel', action: 'list'
	}
}
