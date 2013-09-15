package ch.freebo



import org.junit.*
import grails.test.mixin.*

@TestFor(ShoppingController)
@Mock(Shopping)
class ShoppingControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/shopping/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.shoppingInstanceList.size() == 0
        assert model.shoppingInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.shoppingInstance != null
    }

    void testSave() {
        controller.save()

        assert model.shoppingInstance != null
        assert view == '/shopping/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/shopping/show/1'
        assert controller.flash.message != null
        assert Shopping.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/shopping/list'

        populateValidParams(params)
        def shopping = new Shopping(params)

        assert shopping.save() != null

        params.id = shopping.id

        def model = controller.show()

        assert model.shoppingInstance == shopping
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/shopping/list'

        populateValidParams(params)
        def shopping = new Shopping(params)

        assert shopping.save() != null

        params.id = shopping.id

        def model = controller.edit()

        assert model.shoppingInstance == shopping
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/shopping/list'

        response.reset()

        populateValidParams(params)
        def shopping = new Shopping(params)

        assert shopping.save() != null

        // test invalid parameters in update
        params.id = shopping.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/shopping/edit"
        assert model.shoppingInstance != null

        shopping.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/shopping/show/$shopping.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        shopping.clearErrors()

        populateValidParams(params)
        params.id = shopping.id
        params.version = -1
        controller.update()

        assert view == "/shopping/edit"
        assert model.shoppingInstance != null
        assert model.shoppingInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/shopping/list'

        response.reset()

        populateValidParams(params)
        def shopping = new Shopping(params)

        assert shopping.save() != null
        assert Shopping.count() == 1

        params.id = shopping.id

        controller.delete()

        assert Shopping.count() == 0
        assert Shopping.get(shopping.id) == null
        assert response.redirectedUrl == '/shopping/list'
    }
}
