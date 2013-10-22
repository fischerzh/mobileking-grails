package ch.freebo



import org.junit.*
import grails.test.mixin.*

@TestFor(RetailerController)
@Mock(Retailer)
class RetailerControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/retailer/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.retailerInstanceList.size() == 0
        assert model.retailerInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.retailerInstance != null
    }

    void testSave() {
        controller.save()

        assert model.retailerInstance != null
        assert view == '/retailer/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/retailer/show/1'
        assert controller.flash.message != null
        assert Retailer.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/retailer/list'

        populateValidParams(params)
        def retailer = new Retailer(params)

        assert retailer.save() != null

        params.id = retailer.id

        def model = controller.show()

        assert model.retailerInstance == retailer
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/retailer/list'

        populateValidParams(params)
        def retailer = new Retailer(params)

        assert retailer.save() != null

        params.id = retailer.id

        def model = controller.edit()

        assert model.retailerInstance == retailer
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/retailer/list'

        response.reset()

        populateValidParams(params)
        def retailer = new Retailer(params)

        assert retailer.save() != null

        // test invalid parameters in update
        params.id = retailer.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/retailer/edit"
        assert model.retailerInstance != null

        retailer.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/retailer/show/$retailer.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        retailer.clearErrors()

        populateValidParams(params)
        params.id = retailer.id
        params.version = -1
        controller.update()

        assert view == "/retailer/edit"
        assert model.retailerInstance != null
        assert model.retailerInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/retailer/list'

        response.reset()

        populateValidParams(params)
        def retailer = new Retailer(params)

        assert retailer.save() != null
        assert Retailer.count() == 1

        params.id = retailer.id

        controller.delete()

        assert Retailer.count() == 0
        assert Retailer.get(retailer.id) == null
        assert response.redirectedUrl == '/retailer/list'
    }
}
