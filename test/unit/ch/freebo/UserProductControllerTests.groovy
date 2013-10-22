package ch.freebo



import org.junit.*
import grails.test.mixin.*

@TestFor(UserProductController)
@Mock(UserProduct)
class UserProductControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/userProduct/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.userProductInstanceList.size() == 0
        assert model.userProductInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.userProductInstance != null
    }

    void testSave() {
        controller.save()

        assert model.userProductInstance != null
        assert view == '/userProduct/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/userProduct/show/1'
        assert controller.flash.message != null
        assert UserProduct.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/userProduct/list'

        populateValidParams(params)
        def userProduct = new UserProduct(params)

        assert userProduct.save() != null

        params.id = userProduct.id

        def model = controller.show()

        assert model.userProductInstance == userProduct
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/userProduct/list'

        populateValidParams(params)
        def userProduct = new UserProduct(params)

        assert userProduct.save() != null

        params.id = userProduct.id

        def model = controller.edit()

        assert model.userProductInstance == userProduct
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/userProduct/list'

        response.reset()

        populateValidParams(params)
        def userProduct = new UserProduct(params)

        assert userProduct.save() != null

        // test invalid parameters in update
        params.id = userProduct.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/userProduct/edit"
        assert model.userProductInstance != null

        userProduct.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/userProduct/show/$userProduct.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        userProduct.clearErrors()

        populateValidParams(params)
        params.id = userProduct.id
        params.version = -1
        controller.update()

        assert view == "/userProduct/edit"
        assert model.userProductInstance != null
        assert model.userProductInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/userProduct/list'

        response.reset()

        populateValidParams(params)
        def userProduct = new UserProduct(params)

        assert userProduct.save() != null
        assert UserProduct.count() == 1

        params.id = userProduct.id

        controller.delete()

        assert UserProduct.count() == 0
        assert UserProduct.get(userProduct.id) == null
        assert response.redirectedUrl == '/userProduct/list'
    }
}
