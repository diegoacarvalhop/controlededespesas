package br.com.controlededespesas.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.com.controlededespesas.models.FormaPagamento;
import br.com.controlededespesas.services.FormaPagamentoService;

@Component
public class StringToFormaPagamentoConverter implements Converter<String, FormaPagamento> {

	@Autowired
	private FormaPagamentoService service;

	@Override
	public FormaPagamento convert(String text) {
		if (text.isEmpty()) {
			return null;
		}
		Long id = Long.valueOf(text);
		return service.buscarPorId(id);
	}
}
