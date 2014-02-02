<%@ page import="ch.freebo.UserRanking" %>



<div class="fieldcontain ${hasErrors(bean: userRankingInstance, field: 'pointOfSales', 'error')} ">
	<label for="pointOfSales">
		<g:message code="userRanking.pointOfSales.label" default="Point Of Sales" />
		
	</label>
	<g:select id="pointOfSales" name="pointOfSales.id" from="${ch.freebo.Retailer.list()}" optionKey="id" value="${userRankingInstance?.pointOfSales?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userRankingInstance, field: 'newRank', 'error')} ">
	<label for="newRank">
		<g:message code="userRanking.newRank.label" default="New Rank" />
		
	</label>
	<g:checkBox name="newRank" value="${userRankingInstance?.newRank}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userRankingInstance, field: 'pointsCollected', 'error')} required">
	<label for="pointsCollected">
		<g:message code="userRanking.pointsCollected.label" default="Points Collected" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="pointsCollected" type="number" value="${userRankingInstance.pointsCollected}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: userRankingInstance, field: 'product', 'error')} required">
	<label for="product">
		<g:message code="userRanking.product.label" default="Product" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="product" name="product.id" from="${ch.freebo.Product.list()}" optionKey="id" required="" value="${userRankingInstance?.product?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userRankingInstance, field: 'rank', 'error')} required">
	<label for="rank">
		<g:message code="userRanking.rank.label" default="Rank" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="rank" type="number" value="${userRankingInstance.rank}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: userRankingInstance, field: 'rankBefore', 'error')} required">
	<label for="rankBefore">
		<g:message code="userRanking.rankBefore.label" default="Rank Before" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="rankBefore" type="number" value="${userRankingInstance.rankBefore}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: userRankingInstance, field: 'totalPointsCollected', 'error')} required">
	<label for="totalPointsCollected">
		<g:message code="userRanking.totalPointsCollected.label" default="Total Points Collected" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="totalPointsCollected" type="number" value="${userRankingInstance.totalPointsCollected}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: userRankingInstance, field: 'updated', 'error')} required">
	<label for="updated">
		<g:message code="userRanking.updated.label" default="Updated" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="updated" precision="day"  value="${userRankingInstance?.updated}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: userRankingInstance, field: 'user', 'error')} required">
	<label for="user">
		<g:message code="userRanking.user.label" default="User" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="user" name="user.id" from="${ch.freebo.User.list()}" optionKey="id" required="" value="${userRankingInstance?.user?.id}" class="many-to-one"/>
</div>

