package ch.freebo



import org.junit.*
import grails.test.mixin.*

@TestFor(LoyaltyProgramController)
@Mock(LoyaltyProgram)
class LoyaltyProgramControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/loyaltyProgram/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.loyaltyProgramInstanceList.size() == 0
        assert model.loyaltyProgramInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.loyaltyProgramInstance != null
    }

    void testSave() {
        controller.save()

        assert model.loyaltyProgramInstance != null
        assert view == '/loyaltyProgram/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/loyaltyProgram/show/1'
        assert controller.flash.message != null
        assert LoyaltyProgram.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/loyaltyProgram/list'

        populateValidParams(params)
        def loyaltyProgram = new LoyaltyProgram(params)

        assert loyaltyProgram.save() != null

        params.id = loyaltyProgram.id

        def model = controller.show()

        assert model.loyaltyProgramInstance == loyaltyProgram
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/loyaltyProgram/list'

        populateValidParams(params)
        def loyaltyProgram = new LoyaltyProgram(params)

        assert loyaltyProgram.save() != null

        params.id = loyaltyProgram.id

        def model = controller.edit()

        assert model.loyaltyProgramInstance == loyaltyProgram
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/loyaltyProgram/list'

        response.reset()

        populateValidParams(params)
        def loyaltyProgram = new LoyaltyProgram(params)

        assert loyaltyProgram.save() != null

        // test invalid parameters in update
        params.id = loyaltyProgram.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/loyaltyProgram/edit"
        assert model.loyaltyProgramInstance != null

        loyaltyProgram.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/loyaltyProgram/show/$loyaltyProgram.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        loyaltyProgram.clearErrors()

        populateValidParams(params)
        params.id = loyaltyProgram.id
        params.version = -1
        controller.update()

        assert view == "/loyaltyProgram/edit"
        assert model.loyaltyProgramInstance != null
        assert model.loyaltyProgramInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/loyaltyProgram/list'

        response.reset()

        populateValidParams(params)
        def loyaltyProgram = new LoyaltyProgram(params)

        assert loyaltyProgram.save() != null
        assert LoyaltyProgram.count() == 1

        params.id = loyaltyProgram.id

        controller.delete()

        assert LoyaltyProgram.count() == 0
        assert LoyaltyProgram.get(loyaltyProgram.id) == null
        assert response.redirectedUrl == '/loyaltyProgram/list'
    }
}
