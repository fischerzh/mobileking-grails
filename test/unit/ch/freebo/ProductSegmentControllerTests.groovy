package ch.freebo



import org.junit.*
import grails.test.mixin.*

@TestFor(ProductSegmentController)
@Mock(ProductSegment)
class ProductSegmentControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/productSegment/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.productSegmentInstanceList.size() == 0
        assert model.productSegmentInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.productSegmentInstance != null
    }

    void testSave() {
        controller.save()

        assert model.productSegmentInstance != null
        assert view == '/productSegment/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/productSegment/show/1'
        assert controller.flash.message != null
        assert ProductSegment.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/productSegment/list'

        populateValidParams(params)
        def productSegment = new ProductSegment(params)

        assert productSegment.save() != null

        params.id = productSegment.id

        def model = controller.show()

        assert model.productSegmentInstance == productSegment
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/productSegment/list'

        populateValidParams(params)
        def productSegment = new ProductSegment(params)

        assert productSegment.save() != null

        params.id = productSegment.id

        def model = controller.edit()

        assert model.productSegmentInstance == productSegment
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/productSegment/list'

        response.reset()

        populateValidParams(params)
        def productSegment = new ProductSegment(params)

        assert productSegment.save() != null

        // test invalid parameters in update
        params.id = productSegment.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/productSegment/edit"
        assert model.productSegmentInstance != null

        productSegment.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/productSegment/show/$productSegment.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        productSegment.clearErrors()

        populateValidParams(params)
        params.id = productSegment.id
        params.version = -1
        controller.update()

        assert view == "/productSegment/edit"
        assert model.productSegmentInstance != null
        assert model.productSegmentInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/productSegment/list'

        response.reset()

        populateValidParams(params)
        def productSegment = new ProductSegment(params)

        assert productSegment.save() != null
        assert ProductSegment.count() == 1

        params.id = productSegment.id

        controller.delete()

        assert ProductSegment.count() == 0
        assert ProductSegment.get(productSegment.id) == null
        assert response.redirectedUrl == '/productSegment/list'
    }
}
