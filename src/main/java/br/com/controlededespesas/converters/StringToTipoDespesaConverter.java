package br.com.controlededespesas.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import br.com.controlededespesas.models.TipoDespesa;
import br.com.controlededespesas.services.TipoDespesaService;

@Component
public class StringToTipoDespesaConverter implements Converter<String, TipoDespesa> {

	@Autowired
	private TipoDespesaService service;

	@Override
	public TipoDespesa convert(String text) {
		if (text.isEmpty()) {
			return null;
		}
		Long id = Long.valueOf(text);
		return service.buscarPorId(id);
	}
}
