import ch.freebo.Location
import ch.freebo.Manufacturer;
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
		
		def admin = new User(username: 'admin', enabled: true, password: 'test')
		admin.save(flush: true)
		
		def user = new User(username: 'test', enabled: true, password: 'test')
		user.save(flush: true)
		
		def manuf = new User(username: 'manuf', enabled: true, password: 'test')
		manuf.save(flush: true)
  
		UserRole.create admin, adminRole, true
		UserRole.create user, userRole, true
		UserRole.create manuf, manufRole, true
		
		def prodSegment1 = ProductSegment.findByName("Lebensmittel / Getraenke / Tabakwaren")?:new ProductSegment(name:"Lebensmittel / Getraenke / Tabakwaren").save(flush:true)
		
		def prodCategory1 = ProductCategory.findByName("Getraenke")?:new ProductCategory(name:"Getraenke",productSegment:prodSegment1).save(flush:true)
		def prodCategory2 = ProductCategory.findByName("Snacks")?:new ProductCategory(name:"Snacks",productSegment:prodSegment1).save(flush:true)
		def prodCategory3 = ProductCategory.findByName("Gemuese")?:new ProductCategory(name:"Gemuese",productSegment:prodSegment1).save(flush:true)
		
		def colaCompany = Manufacturer.findByName('Coca Cola Company')?:new Manufacturer(name:'Coca Cola Company').save(failOnError:true)
		def zweifelCompany = Manufacturer.findByName('Zweifel Pomy Chips')?:new Manufacturer(name:'Zweifel Pomy Chips').save(failOnError:true)
		def rivellaCompany = Manufacturer.findByName('Rivella')?:new Manufacturer(name:'Rivella').save(failOnError:true)
		def valserCompany = Manufacturer.findByName('Valser')?:new Manufacturer(name:'Valser').save(failOnError:true)
		
		
		def rivella = Product.findByName("Rivella")?:new Product(name:"Rivella",ean:"7610097111072", productCategory:prodCategory1, imageLink:"http://www.codecheck.info/img/69696/1", manufacturer:rivellaCompany).save(flush:true)
		def zweifel = Product.findByName("Zweifel Chips")?:new Product(name:"Zweifel Chips",ean:"12341241", productCategory:prodCategory2, imageLink:"http://www.codecheck.info/img/55405/1", manufacturer:zweifelCompany).save(flush:true)
		def colazero = Product.findByName("Cola Zero")?:new Product(name:"Cola Zero",ean:"5449000131836", productCategory:prodCategory1, imageLink:"http://www.codecheck.info/img/10189/1", manufacturer:colaCompany).save(flush:true)
		
		def location = Location.findByName("Zuerich")?:new Location(name:"Zuerich", plz:8000).save(flush:true)
		def migros = Retailer.findByName("Migros")?:new Retailer(name:"Migros", location:location).save(flush:true)
		
		def date = new Date()
		def shoppingList = Shopping.findByLocationAndRetailer(location, migros)?:new Shopping(date:date, location:location, retailer:migros, user:user).save(failOnError:true)
		println shoppingList
//		shoppingList.addToProducts(product1)
//		shoppingList.addToProducts(product2)
//		shoppingList.save(flush:true)
		
		def rivellaKauf = new ProductShoppings(product:rivella, shopping:shoppingList, qty:2).save(failOnError:true)
		def zweifelKauf = new ProductShoppings(product:zweifel, shopping:shoppingList, qty:3).save(failOnError:true)
		def colaKauf = new ProductShoppings(product:colazero, shopping:shoppingList, qty:10).save(failOnError:true)
		
		
//		testUser.addToShoppings(shoppingList).save(failOnError:true)
    }
    def destroy = {
    }
}
