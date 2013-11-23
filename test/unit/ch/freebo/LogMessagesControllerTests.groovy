package ch.freebo



import org.junit.*
import grails.test.mixin.*

@TestFor(LogMessagesController)
@Mock(LogMessages)
class LogMessagesControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/logMessages/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.logMessagesInstanceList.size() == 0
        assert model.logMessagesInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.logMessagesInstance != null
    }

    void testSave() {
        controller.save()

        assert model.logMessagesInstance != null
        assert view == '/logMessages/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/logMessages/show/1'
        assert controller.flash.message != null
        assert LogMessages.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/logMessages/list'

        populateValidParams(params)
        def logMessages = new LogMessages(params)

        assert logMessages.save() != null

        params.id = logMessages.id

        def model = controller.show()

        assert model.logMessagesInstance == logMessages
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/logMessages/list'

        populateValidParams(params)
        def logMessages = new LogMessages(params)

        assert logMessages.save() != null

        params.id = logMessages.id

        def model = controller.edit()

        assert model.logMessagesInstance == logMessages
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/logMessages/list'

        response.reset()

        populateValidParams(params)
        def logMessages = new LogMessages(params)

        assert logMessages.save() != null

        // test invalid parameters in update
        params.id = logMessages.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/logMessages/edit"
        assert model.logMessagesInstance != null

        logMessages.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/logMessages/show/$logMessages.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        logMessages.clearErrors()

        populateValidParams(params)
        params.id = logMessages.id
        params.version = -1
        controller.update()

        assert view == "/logMessages/edit"
        assert model.logMessagesInstance != null
        assert model.logMessagesInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/logMessages/list'

        response.reset()

        populateValidParams(params)
        def logMessages = new LogMessages(params)

        assert logMessages.save() != null
        assert LogMessages.count() == 1

        params.id = logMessages.id

        controller.delete()

        assert LogMessages.count() == 0
        assert LogMessages.get(logMessages.id) == null
        assert response.redirectedUrl == '/logMessages/list'
    }
}
