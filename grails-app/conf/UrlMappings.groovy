class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
		
		"/product/loginFromApp"(controller: "product", action: "loginFromApp")
		"/product/updateUserInfo"(controller: "product", action: "updateUserInfo")
		"/product/sendMessage"(controller: "product", action: "callGCMService")
		"/controlPanel/sendMessage"(controller: "controlPanel", action: "callGCMService")
	}
}
