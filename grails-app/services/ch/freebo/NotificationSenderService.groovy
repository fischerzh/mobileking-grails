package ch.freebo

class NotificationSenderService {
	
	def androidGcmService
	
	String message
	User user

	def callGCMService()
	{
		println params
		sendMessage();
	}
	
	def callGCMServiceMsg(String message, User user)
	{
		this.message = message
		this.user = user
		sendMessage();
	}
	
	
	def sendMessage = {
		def deviceList
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
				currentMessages << [ "1" : params.inputMessage]
		}
		
		flash.message = message(code: 'default.created.message', args: [message(code: 'controlPanel.label', default: 'ControlPanel Message verschickt: '), params.inputMessage])
		
		androidGcmService.sendMessage(messages, params.deviceToken,"", grailsApplication.config.android.gcm.api.key).toString()
		if(params.user)
			return
		else
			redirect action: 'list'
	}
}
