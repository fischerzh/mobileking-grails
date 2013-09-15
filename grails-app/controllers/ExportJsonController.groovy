import java.beans.Encoder;

import ch.freebo.Product
import grails.converters.JSON
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import grails.plugins.springsecurity.Secured

class ExportJsonController {
	
	def exportService
	
	def exportJson()
	{		
		def List<Product> produktListe = []
		
		if(username == null)
		{
			flash.message = 'Bitte Gemeinde waehlen'
			redirect action: 'index'
			return
		}
		
		HashMap jsonMap = new HashMap()
				
		jsonMap.products = produktListe.unique().collect {prod ->
			def produktname
			def bemerkung
			def produktfamilie
			def verteiler
			def hersteller

			return [id: prod.id, ean: prod.ean, name: produktname, producer: prod.hersteller?hersteller:" ", category: prod.produktfamilie!=null?produktfamilie:" ", 
				contents: prod.inhaltsstoffe?prod.inhaltsstoffe.name:null, packaging: prod.verpackung?prod.verpackung.verpackungsart():" ", 
				controller: prod.kontrolleur?prod.kontrolleur.name:" ", comment: bemerkung, prodfamcomment: prod.bemerkung_produkt,  chalavakum: prod.istChalavakum, parve: prod.parve, 
				verteiler: prod.verteiler!=null?verteiler:" "]
		}
						
		def jsonPretty
		def json
		response.contentType = grailsApplication.config.grails.mime.types["json"] + "UTF-8"
		response.setHeader("Content-disposition", "attachment; filename=${gemeinde_name.fileName}.json")
		response.setCharacterEncoding("UTF-8")
		
		println "JSON Map: "+ jsonMap
		
		json = new JsonBuilder(jsonMap)
		
		jsonPretty = JsonOutput.prettyPrint(json.toString())
		
		jsonPretty = ("["+jsonPretty+"]")
				
		response.outputStream << jsonPretty.toString()
		return
	}
}