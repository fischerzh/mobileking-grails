import ch.freebo.DataGeneratorService

class UrlMappings {
	
	def DataGeneratorService dataGenerator

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
		
		"/loginFromApp"(controller: "data", action: "loginFromApp")
		"/updateUserInfo"(controller: "data", action: "updateUserInfo")
		"/updateFavorites" (controller: "data", action: "updateFavorites")
		"/updateUserLog"(controller: "data", action: "updateUserLog")
//		"/product/sendMessage"(controller: "product", action: "callGCMService")
		"/controlPanel/sendMessage"(controller: "controlPanel", action: "callGCMService")
		"/user/loggedIn" (controller:"user", action: "loggedIn")
	}
}
