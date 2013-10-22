package ch.freebo



import org.junit.*
import grails.test.mixin.*

@TestFor(ProductShoppingsController)
@Mock(ProductShoppings)
class ProductShoppingsControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/productShoppings/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.productShoppingsInstanceList.size() == 0
        assert model.productShoppingsInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.productShoppingsInstance != null
    }

    void testSave() {
        controller.save()

        assert model.productShoppingsInstance != null
        assert view == '/productShoppings/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/productShoppings/show/1'
        assert controller.flash.message != null
        assert ProductShoppings.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/productShoppings/list'

        populateValidParams(params)
        def productShoppings = new ProductShoppings(params)

        assert productShoppings.save() != null

        params.id = productShoppings.id

        def model = controller.show()

        assert model.productShoppingsInstance == productShoppings
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/productShoppings/list'

        populateValidParams(params)
        def productShoppings = new ProductShoppings(params)

        assert productShoppings.save() != null

        params.id = productShoppings.id

        def model = controller.edit()

        assert model.productShoppingsInstance == productShoppings
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/productShoppings/list'

        response.reset()

        populateValidParams(params)
        def productShoppings = new ProductShoppings(params)

        assert productShoppings.save() != null

        // test invalid parameters in update
        params.id = productShoppings.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/productShoppings/edit"
        assert model.productShoppingsInstance != null

        productShoppings.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/productShoppings/show/$productShoppings.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        productShoppings.clearErrors()

        populateValidParams(params)
        params.id = productShoppings.id
        params.version = -1
        controller.update()

        assert view == "/productShoppings/edit"
        assert model.productShoppingsInstance != null
        assert model.productShoppingsInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/productShoppings/list'

        response.reset()

        populateValidParams(params)
        def productShoppings = new ProductShoppings(params)

        assert productShoppings.save() != null
        assert ProductShoppings.count() == 1

        params.id = productShoppings.id

        controller.delete()

        assert ProductShoppings.count() == 0
        assert ProductShoppings.get(productShoppings.id) == null
        assert response.redirectedUrl == '/productShoppings/list'
    }
}
