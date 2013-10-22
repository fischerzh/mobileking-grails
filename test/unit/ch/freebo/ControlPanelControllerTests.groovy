package ch.freebo



import org.junit.*
import grails.test.mixin.*

@TestFor(ControlPanelController)
@Mock(ControlPanel)
class ControlPanelControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/controlPanel/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.controlPanelInstanceList.size() == 0
        assert model.controlPanelInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.controlPanelInstance != null
    }

    void testSave() {
        controller.save()

        assert model.controlPanelInstance != null
        assert view == '/controlPanel/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/controlPanel/show/1'
        assert controller.flash.message != null
        assert ControlPanel.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/controlPanel/list'

        populateValidParams(params)
        def controlPanel = new ControlPanel(params)

        assert controlPanel.save() != null

        params.id = controlPanel.id

        def model = controller.show()

        assert model.controlPanelInstance == controlPanel
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/controlPanel/list'

        populateValidParams(params)
        def controlPanel = new ControlPanel(params)

        assert controlPanel.save() != null

        params.id = controlPanel.id

        def model = controller.edit()

        assert model.controlPanelInstance == controlPanel
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/controlPanel/list'

        response.reset()

        populateValidParams(params)
        def controlPanel = new ControlPanel(params)

        assert controlPanel.save() != null

        // test invalid parameters in update
        params.id = controlPanel.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/controlPanel/edit"
        assert model.controlPanelInstance != null

        controlPanel.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/controlPanel/show/$controlPanel.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        controlPanel.clearErrors()

        populateValidParams(params)
        params.id = controlPanel.id
        params.version = -1
        controller.update()

        assert view == "/controlPanel/edit"
        assert model.controlPanelInstance != null
        assert model.controlPanelInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/controlPanel/list'

        response.reset()

        populateValidParams(params)
        def controlPanel = new ControlPanel(params)

        assert controlPanel.save() != null
        assert ControlPanel.count() == 1

        params.id = controlPanel.id

        controller.delete()

        assert ControlPanel.count() == 0
        assert ControlPanel.get(controlPanel.id) == null
        assert response.redirectedUrl == '/controlPanel/list'
    }
}
