import ch.freebo.DataGeneratorService

class UrlMappings {
	

		static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
		
		"/loginFromApp"(controller: "data", action: "loginFromApp")
		"/updateErrorLogs"(controller: "data", action: "updateErrorLogs")
		"/updateFavorites" (controller: "data", action: "updateFavorites")
		"/updateUserLog"(controller: "data", action: "updateUserLog")
		"/updateUserSettings" (controller: "data", action:  "updateUserSettings")
		"/updateUserFiles" (controller: "data", action: "updateUserFiles")
//		"/product/sendMessage"(controller: "product", action: "callGCMService")
		"/controlPanel/sendMessage"(controller: "controlPanel", action: "callGCMService")
		"/user/loggedIn" (controller:"user", action: "loggedIn")
	}
}
