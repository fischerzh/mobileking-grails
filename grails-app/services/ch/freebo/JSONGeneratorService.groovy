package ch.freebo

import grails.converters.JSON
import groovy.json.JsonBuilder
import grails.plugins.springsecurity.Secured


class JSONGeneratorService {

    def serviceMethod() {

    }
	
	@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
	def loginFromApp() {
		println "ProductController: loginFromApp()"
		println "Params" + params
		def user = User.findByUsername(params.username)
		println "User: " +user
		
		if(user==null)
		{
			response.status = 500
			render([error: 'Error 500'] as JSON)
		}

		println user.shoppings.products.collect()
		println user.loyaltyPrograms.collect()
		
		
		def jsonExport = getJSONData(user)
		
		def json = new JsonBuilder(jsonExport)
				
		println json.toPrettyString()
		//return product and user settings!!
		if(user)
			render json
		else
			render( status: 500, exception: params.exception) as JSON
	}
	
	def getJSONData(User user)
	{
		HashMap jsonMap = new HashMap()
		
		def products = []
		
		def userShopping = user.shoppings.each {
			println "Add products: " +it.products
			products.addAll(it.products)
		}
		
		println userShopping
		
		jsonMap.products = products.unique().collect {prod ->
			//check if user has optIn
			def optIn = hasUserOptIn(prod, user)
			//count products bought
			def pointsCollected = calculatePointsForProduct(prod, user)
			return [id: prod.id, ean: prod.ean, name: prod.name, imagelink: prod.imageLink, optin: optIn, points: pointsCollected]
		}
		
		jsonMap.username = user.username
		
		println jsonMap
		return jsonMap
	}
	
	def hasUserOptIn(Product prod, User user)
	{
		def userProdListOptOut = UserProduct.findAllByProductAndUser(prod, user, [max:1, sort:"optOutDate", order:"desc"])
		def	userProdListOptIn = UserProduct.findAllByProductAndUser(prod, user, [max:1, sort:"optInDate", order:"desc"])
		
		def userProd
		
		if(userProdListOptOut && userProdListOptIn)
			userProd = userProdListOptOut[0].optOutDate > userProdListOptIn[0].optInDate ? userProdListOptOut[0] : userProdListOptIn[0]
		else if(userProdListOptOut)
			userProd = userProdListOptOut[0]
		else if(userProdListOptIn)
			userProd = userProdListOptIn[0]
		
		def optIn = false
		
		if(userProd)
		{
			println "optIn: " +userProd.optIn
			if(userProd.optIn)
				optIn = true
		}

		return optIn
	}

	
	def calculatePointsForProduct(Product prod, User user)
	{
		def nmbr = 0
		def prods = user.shoppings.each { s->
			s.products.each {Product p ->
				if(p == prod)
					nmbr = nmbr+1
			}
		}
		println "calculatePointsForProduct: " +nmbr
		return nmbr
	}
	
	def calculateProductRank(Product prod, User user)
	{
		
		
	}
	
	@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
	def updateUserInfo()
	{
		println "ProductController: updateUserInfo()"
		println "Params" + params
		def user = User.findByUsername(params.username)
		println "User: " +user
		
		if(user==null)
		{
			response.status = 500
			render([error: 'Error 500'] as JSON)
		}
		
		if(params.productid)
		{
			def prod = Product.findById(params.productid)
			
			if(prod)
			{
				println "Found product for Opt-In: " + prod.name
				//OPT IN
				if(params.optin)
				{
					def userProd = new UserProduct(user: user, product: prod, optIn: true)
					println "Opt-in: " +userProd
					if(!userProd.save(failOnError:true))
					{
						render( status: 500, exception: params.exception) as JSON
					}
				}
				//OPT OUT
				else if(params.optout)
				{
					def userProd = new UserProduct(user: user, product: prod, optIn: false )
					println "Opt-Out: " +userProd
					if(!userProd.save(failOnError:true))
					{
						render( status: 500, exception: params.exception) as JSON
					}
				}
				
				render([response: 'OK 201'] as JSON)
				
			}
			else
			{
				println "Product not found for Opt-In!"
			}
		}
		
	}
}
