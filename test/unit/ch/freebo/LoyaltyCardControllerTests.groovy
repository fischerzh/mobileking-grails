package ch.freebo



import org.junit.*
import grails.test.mixin.*

@TestFor(LoyaltyCardController)
@Mock(LoyaltyCard)
class LoyaltyCardControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/loyaltyCard/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.loyaltyCardInstanceList.size() == 0
        assert model.loyaltyCardInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.loyaltyCardInstance != null
    }

    void testSave() {
        controller.save()

        assert model.loyaltyCardInstance != null
        assert view == '/loyaltyCard/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/loyaltyCard/show/1'
        assert controller.flash.message != null
        assert LoyaltyCard.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/loyaltyCard/list'

        populateValidParams(params)
        def loyaltyCard = new LoyaltyCard(params)

        assert loyaltyCard.save() != null

        params.id = loyaltyCard.id

        def model = controller.show()

        assert model.loyaltyCardInstance == loyaltyCard
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/loyaltyCard/list'

        populateValidParams(params)
        def loyaltyCard = new LoyaltyCard(params)

        assert loyaltyCard.save() != null

        params.id = loyaltyCard.id

        def model = controller.edit()

        assert model.loyaltyCardInstance == loyaltyCard
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/loyaltyCard/list'

        response.reset()

        populateValidParams(params)
        def loyaltyCard = new LoyaltyCard(params)

        assert loyaltyCard.save() != null

        // test invalid parameters in update
        params.id = loyaltyCard.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/loyaltyCard/edit"
        assert model.loyaltyCardInstance != null

        loyaltyCard.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/loyaltyCard/show/$loyaltyCard.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        loyaltyCard.clearErrors()

        populateValidParams(params)
        params.id = loyaltyCard.id
        params.version = -1
        controller.update()

        assert view == "/loyaltyCard/edit"
        assert model.loyaltyCardInstance != null
        assert model.loyaltyCardInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/loyaltyCard/list'

        response.reset()

        populateValidParams(params)
        def loyaltyCard = new LoyaltyCard(params)

        assert loyaltyCard.save() != null
        assert LoyaltyCard.count() == 1

        params.id = loyaltyCard.id

        controller.delete()

        assert LoyaltyCard.count() == 0
        assert LoyaltyCard.get(loyaltyCard.id) == null
        assert response.redirectedUrl == '/loyaltyCard/list'
    }
}
