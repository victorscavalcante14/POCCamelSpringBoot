package br.com.poc.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpMethods;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class TesteRouteBuilder extends RouteBuilder {

	private static final String URI_CHAMAR_ENDPOINT = "direct://chamarEndpoint";
	private static final String URI_MONTAR_REQUISICAO = "direct://montarRequisicao";
	private static final String ENDPOINT = "viacep.com.br";
	
	private int maxRetentativas = 3;
	private int delayRetentativa = 3000;
	private int controleThroughput = 1;
	private int periodoControleThroughput = 2000;

	@Override
	public void configure() throws Exception {
		restConfiguration().component("servlet").bindingMode(RestBindingMode.auto);

		rest("/cep")
		.get("{numero}")
			.route()
				.to(URI_MONTAR_REQUISICAO)
		.end();

		from(URI_MONTAR_REQUISICAO).routeId("uriMontarRequisicao")
				.setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.GET))
				.setHeader(Exchange.HTTP_PATH, simple("/ws/${header.numero}/json"))
				
				.throttle(controleThroughput)
				.timePeriodMillis(periodoControleThroughput)
					.to(URI_CHAMAR_ENDPOINT)
		.end();
		
		from(URI_CHAMAR_ENDPOINT).routeId("uriChamarEndpoint")
			.onException(Exception.class).handled(true)
				.maximumRedeliveries(maxRetentativas).redeliveryDelay(delayRetentativa)
				.log(LoggingLevel.ERROR, "[TesteRouteBuilder] Nao foi possivel realizar a requisicao ao endpoint ["+ENDPOINT+"]")
				
	            .setBody(constant("Tente novamente mais tarde"))
			.end()
			
			.log(LoggingLevel.INFO, "[TesteRouteBuilder] Chamando o endpoint utilizando o CEP[${header.numero}]")
			.to("http://"+ENDPOINT+"?bridgeEndpoint=true")
			.log(LoggingLevel.INFO, "[TesteRouteBuilder] Codigo de resposta HTTP [${header.CamelHttpResponseCode}]")
		.end();

	}
}
