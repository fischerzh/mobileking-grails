package ch.freebo



import org.junit.*
import grails.test.mixin.*

@TestFor(LoyaltyProgramLevelsController)
@Mock(LoyaltyProgramLevels)
class LoyaltyProgramLevelsControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/loyaltyProgramLevels/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.loyaltyProgramLevelsInstanceList.size() == 0
        assert model.loyaltyProgramLevelsInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.loyaltyProgramLevelsInstance != null
    }

    void testSave() {
        controller.save()

        assert model.loyaltyProgramLevelsInstance != null
        assert view == '/loyaltyProgramLevels/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/loyaltyProgramLevels/show/1'
        assert controller.flash.message != null
        assert LoyaltyProgramLevels.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/loyaltyProgramLevels/list'

        populateValidParams(params)
        def loyaltyProgramLevels = new LoyaltyProgramLevels(params)

        assert loyaltyProgramLevels.save() != null

        params.id = loyaltyProgramLevels.id

        def model = controller.show()

        assert model.loyaltyProgramLevelsInstance == loyaltyProgramLevels
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/loyaltyProgramLevels/list'

        populateValidParams(params)
        def loyaltyProgramLevels = new LoyaltyProgramLevels(params)

        assert loyaltyProgramLevels.save() != null

        params.id = loyaltyProgramLevels.id

        def model = controller.edit()

        assert model.loyaltyProgramLevelsInstance == loyaltyProgramLevels
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/loyaltyProgramLevels/list'

        response.reset()

        populateValidParams(params)
        def loyaltyProgramLevels = new LoyaltyProgramLevels(params)

        assert loyaltyProgramLevels.save() != null

        // test invalid parameters in update
        params.id = loyaltyProgramLevels.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/loyaltyProgramLevels/edit"
        assert model.loyaltyProgramLevelsInstance != null

        loyaltyProgramLevels.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/loyaltyProgramLevels/show/$loyaltyProgramLevels.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        loyaltyProgramLevels.clearErrors()

        populateValidParams(params)
        params.id = loyaltyProgramLevels.id
        params.version = -1
        controller.update()

        assert view == "/loyaltyProgramLevels/edit"
        assert model.loyaltyProgramLevelsInstance != null
        assert model.loyaltyProgramLevelsInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/loyaltyProgramLevels/list'

        response.reset()

        populateValidParams(params)
        def loyaltyProgramLevels = new LoyaltyProgramLevels(params)

        assert loyaltyProgramLevels.save() != null
        assert LoyaltyProgramLevels.count() == 1

        params.id = loyaltyProgramLevels.id

        controller.delete()

        assert LoyaltyProgramLevels.count() == 0
        assert LoyaltyProgramLevels.get(loyaltyProgramLevels.id) == null
        assert response.redirectedUrl == '/loyaltyProgramLevels/list'
    }
}
