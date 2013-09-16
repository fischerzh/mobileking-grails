import ch.freebo.Location
import ch.freebo.Product
import ch.freebo.ProductCategory
import ch.freebo.ProductSegment
import ch.freebo.ProductShoppings
import ch.freebo.Retailer
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
		
		def location = Location.findByName("Zuerich")?:new Location(name:"Zuerich", plz:8000).save(flush:true)
		def migros = Retailer.findByName("Migros")?:new Retailer(name:"Migros", location:location).save(flush:true)
		
		def date = new Date()
		def shoppingList = Shopping.findByLocationAndRetailer(location, migros)?:new Shopping(date:date, location:location, retailer:migros, user:testUser).save(flush:true)
		println shoppingList
//		shoppingList.addToProducts(product1)
//		shoppingList.addToProducts(product2)
//		shoppingList.save(flush:true)
		
		def rivellaKauf = new ProductShoppings(product:product1, shopping:shoppingList, qty:2).save(failOnError:true)
		def zweifelKauf = new ProductShoppings(product:product2, shopping:shoppingList, qty:3).save(failOnError:true)
		
		
		testUser.addToShoppings(shoppingList).save(flush:true)
    }
    def destroy = {
    }
}
