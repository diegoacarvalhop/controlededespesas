package br.com.controlededespesas.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.com.controlededespesas.models.TipoPagamento;
import br.com.controlededespesas.services.TipoPagamentoService;

@Component
public class StringToTipoPagamentoConverter implements Converter<String, TipoPagamento> {

	@Autowired
	private TipoPagamentoService service;

	@Override
	public TipoPagamento convert(String text) {
		if (text.isEmpty()) {
			return null;
		}
		Long id = Long.valueOf(text);
		return service.buscarPorId(id);
	}
}
