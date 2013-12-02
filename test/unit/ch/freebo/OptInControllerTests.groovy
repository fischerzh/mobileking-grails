package ch.freebo



import org.junit.*
import grails.test.mixin.*

@TestFor(OptInController)
@Mock(OptIn)
class OptInControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/optIn/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.optInInstanceList.size() == 0
        assert model.optInInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.optInInstance != null
    }

    void testSave() {
        controller.save()

        assert model.optInInstance != null
        assert view == '/optIn/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/optIn/show/1'
        assert controller.flash.message != null
        assert OptIn.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/optIn/list'

        populateValidParams(params)
        def optIn = new OptIn(params)

        assert optIn.save() != null

        params.id = optIn.id

        def model = controller.show()

        assert model.optInInstance == optIn
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/optIn/list'

        populateValidParams(params)
        def optIn = new OptIn(params)

        assert optIn.save() != null

        params.id = optIn.id

        def model = controller.edit()

        assert model.optInInstance == optIn
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/optIn/list'

        response.reset()

        populateValidParams(params)
        def optIn = new OptIn(params)

        assert optIn.save() != null

        // test invalid parameters in update
        params.id = optIn.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/optIn/edit"
        assert model.optInInstance != null

        optIn.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/optIn/show/$optIn.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        optIn.clearErrors()

        populateValidParams(params)
        params.id = optIn.id
        params.version = -1
        controller.update()

        assert view == "/optIn/edit"
        assert model.optInInstance != null
        assert model.optInInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/optIn/list'

        response.reset()

        populateValidParams(params)
        def optIn = new OptIn(params)

        assert optIn.save() != null
        assert OptIn.count() == 1

        params.id = optIn.id

        controller.delete()

        assert OptIn.count() == 0
        assert OptIn.get(optIn.id) == null
        assert response.redirectedUrl == '/optIn/list'
    }
}
