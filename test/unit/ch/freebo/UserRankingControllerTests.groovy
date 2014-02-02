package ch.freebo



import org.junit.*
import grails.test.mixin.*

@TestFor(UserRankingController)
@Mock(UserRanking)
class UserRankingControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/userRanking/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.userRankingInstanceList.size() == 0
        assert model.userRankingInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.userRankingInstance != null
    }

    void testSave() {
        controller.save()

        assert model.userRankingInstance != null
        assert view == '/userRanking/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/userRanking/show/1'
        assert controller.flash.message != null
        assert UserRanking.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/userRanking/list'

        populateValidParams(params)
        def userRanking = new UserRanking(params)

        assert userRanking.save() != null

        params.id = userRanking.id

        def model = controller.show()

        assert model.userRankingInstance == userRanking
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/userRanking/list'

        populateValidParams(params)
        def userRanking = new UserRanking(params)

        assert userRanking.save() != null

        params.id = userRanking.id

        def model = controller.edit()

        assert model.userRankingInstance == userRanking
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/userRanking/list'

        response.reset()

        populateValidParams(params)
        def userRanking = new UserRanking(params)

        assert userRanking.save() != null

        // test invalid parameters in update
        params.id = userRanking.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/userRanking/edit"
        assert model.userRankingInstance != null

        userRanking.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/userRanking/show/$userRanking.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        userRanking.clearErrors()

        populateValidParams(params)
        params.id = userRanking.id
        params.version = -1
        controller.update()

        assert view == "/userRanking/edit"
        assert model.userRankingInstance != null
        assert model.userRankingInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/userRanking/list'

        response.reset()

        populateValidParams(params)
        def userRanking = new UserRanking(params)

        assert userRanking.save() != null
        assert UserRanking.count() == 1

        params.id = userRanking.id

        controller.delete()

        assert UserRanking.count() == 0
        assert UserRanking.get(userRanking.id) == null
        assert response.redirectedUrl == '/userRanking/list'
    }
}
