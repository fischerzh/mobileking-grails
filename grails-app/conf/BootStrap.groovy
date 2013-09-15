import ch.freebo.Product
import ch.freebo.ProductCategory
import ch.freebo.ProductSegment
import ch.freebo.Role
import ch.freebo.Shopping
import ch.freebo.User
import ch.freebo.UserRole

class BootStrap {

    def init = { servletContext ->
		
		def adminRole = new Role(authority: 'ROLE_ADMIN').save(flush: true)
		def userRole = new Role(authority: 'ROLE_USER').save(flush: true)
		def manufRole = new Role(authority: 'ROLE_MANUF').save(flush: true)
		
		def testUser = new User(username: 'test', enabled: true, password: 'test')
		testUser.save(flush: true)
  
		UserRole.create testUser, adminRole, true
		
		def prodSegment1 = ProductSegment.findByName("Lebensmittel / Getraenke / Tabakwaren")?:new ProductSegment(name:"Lebensmittel / Getraenke / Tabakwaren").save(flush:true)
		
		def prodCategory1 = ProductCategory.findByName("Getraenke")?:new ProductCategory(name:"Getraenke",productSegment:prodSegment1).save(flush:true)
		def prodCategory2 = ProductCategory.findByName("Snacks")?:new ProductCategory(name:"Snacks",productSegment:prodSegment1).save(flush:true)
		def prodCategory3 = ProductCategory.findByName("Gemuese")?:new ProductCategory(name:"Gemuese",productSegment:prodSegment1).save(flush:true)
		
		def product1 = Product.findByName("Rivella")?:new Product(name:"Rivella",ean:"12341241", productCategory:prodCategory1, imageLink:"http://www.codecheck.info/img/69696/1").save(flush:true)
		def product2 = Product.findByName("Zweifel Chips")?:new Product(name:"Zweifel Chips",ean:"12341241", productCategory:prodCategory2, imageLink:"http://www.codecheck.info/img/55405/1").save(flush:true)
		
		def date = new Date()
		def shoppingList = Shopping.findByPlace("Migros Brunaupark")?:new Shopping(date:date, place:"Migros Brunaupark").save(flush:true)
		
		shoppingList.addToProducts(product1)
		shoppingList.addToProducts(product2)
		shoppingList.save(flush:true)
		
		testUser.addToShoppings(shoppingList).save(flush:true)
    }
    def destroy = {
    }
}
