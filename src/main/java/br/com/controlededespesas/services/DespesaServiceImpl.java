package br.com.controlededespesas.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.controlededespesas.models.ControleDespesa;
import br.com.controlededespesas.models.Despesa;
import br.com.controlededespesas.models.Usuario;
import br.com.controlededespesas.repositories.DespesaRepository;
import br.com.controlededespesas.utils.PaginacaoUtil;

@Service
@Transactional(readOnly = false)
public class DespesaServiceImpl implements DespesaService {

	@Autowired
	private DespesaRepository repository;

	@Autowired
	ControleDespesaService serviceControleDespesa;

	@Autowired
	private UsuarioService serviceUsuario;

	@Override
	public void salvar(Despesa despesa) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		despesa.setUsuario(usuario);
		despesa.setDescricao(despesa.getDescricao().toUpperCase());
		if (despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("DÉBITO")
				|| despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("DEBITO")) {
			despesa.setStatus("QUITADO");
		} else {
			despesa.setStatus("VIGENTE");
		}
		repository.save(despesa);
		ControleDespesa controleDespesa = null;
		if (despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("CRÉDITO")
				|| despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("CREDITO")) {
			for (int x = 0; x < despesa.getQuantidadeParcelas(); x++) {
				controleDespesa = new ControleDespesa();
				controleDespesa.setDespesa(despesa);
				controleDespesa.setValor(
						despesa.getValor().divide(new BigDecimal(despesa.getQuantidadeParcelas()), 2, RoundingMode.UP));
				controleDespesa.setStatus(despesa.getStatus());
				controleDespesa.setParcela(x + 1);
				if (despesa.getDataDespesa().getDayOfMonth() < despesa.getFormaPagamento().getDiaMelhorCompra()) {
					controleDespesa.setDataParcela(
							(LocalDate.of(despesa.getDataDespesa().getYear(), despesa.getDataDespesa().getMonth(),
									despesa.getFormaPagamento().getDiaVencimentoFatura())).plusMonths(x));
				} else {
					controleDespesa.setDataParcela(
							(LocalDate.of(despesa.getDataDespesa().getYear(), despesa.getDataDespesa().getMonth(),
									despesa.getFormaPagamento().getDiaVencimentoFatura())).plusMonths(x + 1));
				}
				serviceControleDespesa.salvar(controleDespesa);
			}
		}
	}

	@Override
	public void editar(Despesa despesa) {
		Despesa despesaAnterior = buscarPorId(despesa.getId());
		if (despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("DÉBITO")
				|| despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("DEBITO")
				|| despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("DINHEIRO")) {
			despesa.setStatus("QUITADO");
		} else if (despesa.getControleDespesas() != null) {
			if (despesa.getControleDespesas().get(despesa.getControleDespesas().size() - 1).getStatus()
					.equals("VIGENTE")) {
				despesa.setStatus("VIGENTE");
			} else {
				despesa.setStatus("QUITADO");
			}
		} else {
			despesa.setStatus("VIGENTE");
		}
		Collections.sort(despesaAnterior.getControleDespesas(), new Comparator<ControleDespesa>() {
			public int compare(ControleDespesa p1, ControleDespesa p2) {
				return p1.getParcela().compareTo(p2.getParcela());
			}
		});

		ControleDespesa controleDespesaAdicionar = null;

		// Validação se mudou a quantidade de parcelas
		if (despesa.getQuantidadeParcelas() < despesaAnterior.getQuantidadeParcelas()) {
			for (ControleDespesa controleDespesaExcluir : despesaAnterior.getControleDespesas()) {
				if (controleDespesaExcluir.getParcela() > despesa.getQuantidadeParcelas()) {
					serviceControleDespesa.excluir(controleDespesaExcluir.getId());
				}
			}
		} else if (despesa.getQuantidadeParcelas() > despesaAnterior.getControleDespesas().size()) {
			for (int x = despesaAnterior.getControleDespesas().size(); x < despesa.getQuantidadeParcelas(); x++) {
				controleDespesaAdicionar = new ControleDespesa();
				controleDespesaAdicionar.setDespesa(despesa);
				if (despesaAnterior.getControleDespesas().size() != 0) {
					controleDespesaAdicionar.setValor(despesa.getValor()
							.divide(new BigDecimal(despesa.getQuantidadeParcelas()), 2, RoundingMode.UP));
					controleDespesaAdicionar.setDataParcela((LocalDate
							.of(despesa.getDataDespesa().getYear(), despesa.getDataDespesa().getMonth(),
									despesaAnterior.getControleDespesas().get(0).getDataParcela().getDayOfMonth())
							.plusMonths(x + 1)));
				} else {
					controleDespesaAdicionar.setValor(despesa.getValor()
							.divide(new BigDecimal(despesa.getQuantidadeParcelas()), 2, RoundingMode.UP));
					if (despesa.getDataDespesa().getDayOfMonth() < despesa.getFormaPagamento().getDiaMelhorCompra()) {
						controleDespesaAdicionar.setDataParcela(
								(LocalDate.of(despesa.getDataDespesa().getYear(), despesa.getDataDespesa().getMonth(),
										despesa.getFormaPagamento().getDiaVencimentoFatura())).plusMonths(x));
					} else {
						controleDespesaAdicionar.setDataParcela(
								(LocalDate.of(despesa.getDataDespesa().getYear(), despesa.getDataDespesa().getMonth(),
										despesa.getFormaPagamento().getDiaVencimentoFatura())).plusMonths(x + 1));
					}
				}
				controleDespesaAdicionar.setParcela(x + 1);
				controleDespesaAdicionar.setStatus("VIGENTE");
				serviceControleDespesa.salvar(controleDespesaAdicionar);
			}
		}

		if (despesa.getQuantidadeParcelas() < despesaAnterior.getQuantidadeParcelas()) {
			if (despesa.getQuantidadeParcelas() == 0) {
				despesaAnterior.setControleDespesas(new ArrayList<>());
			} else {
				int y = despesa.getQuantidadeParcelas();
				List<ControleDespesa> controleDespesas = despesaAnterior.getControleDespesas();
				for (int x = 0; x < controleDespesas.size(); x++) {
					if (controleDespesas.get(x).getParcela() == (y + 1)) {
						controleDespesas.remove(x);
						x = 0;
						y++;
					}
				}
				despesaAnterior.setControleDespesas(controleDespesas);
			}
		}

		// Validação se mudou o valor da despesa
		if (despesa.getValor().intValue() != despesaAnterior.getValor().intValue()
				|| despesa.getQuantidadeParcelas() != despesaAnterior.getQuantidadeParcelas()) {
			for (ControleDespesa controleDespesa : despesaAnterior.getControleDespesas()) {
				controleDespesa.setValor(
						despesa.getValor().divide(new BigDecimal(despesa.getQuantidadeParcelas()), 2, RoundingMode.UP));
				serviceControleDespesa.editar(controleDespesa, false);
			}
		}

		// Validação se mudou a data da despesa
		if ((!despesa.getDataDespesa().toString().equals(despesaAnterior.getDataDespesa().toString()))
				|| (despesa.getFormaPagamento() != despesaAnterior.getFormaPagamento())) {
			if (despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("CRÉDITO")
					|| despesa.getFormaPagamento().getTipoPagamento().getDescricao().equals("CREDITO")) {
				int x = 0;
				for (ControleDespesa controleDespesa : despesaAnterior.getControleDespesas()) {
					if (despesa.getDataDespesa().getDayOfMonth() < despesa.getFormaPagamento().getDiaMelhorCompra()) {
						controleDespesa.setDataParcela(
								(LocalDate.of(despesa.getDataDespesa().getYear(), despesa.getDataDespesa().getMonth(),
										despesa.getFormaPagamento().getDiaVencimentoFatura())).plusMonths(x));
						controleDespesa.setStatus("VIGENTE");
					} else {
						controleDespesa.setDataParcela(
								(LocalDate.of(despesa.getDataDespesa().getYear(), despesa.getDataDespesa().getMonth(),
										despesa.getFormaPagamento().getDiaVencimentoFatura())).plusMonths(x + 1));
					}
					serviceControleDespesa.editar(controleDespesa, false);
					x++;
				}
			}
		}

		repository.update(despesa);

	}

	@Override
	public void excluir(Long id) {
		repository.delete(id);
	}

	@Override
	public Despesa buscarPorId(Long id) {
		Despesa despesa = repository.findById(id);

		Collections.sort(despesa.getControleDespesas(), new Comparator<ControleDespesa>() {
			public int compare(ControleDespesa p1, ControleDespesa p2) {
				return p1.getParcela().compareTo(p2.getParcela());
			}
		});

		return despesa;
	}

	@Override
	public List<Despesa> buscarTodos() {
		Usuario usuario = null;
		List<Despesa> despesas = null;
		if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			usuario = serviceUsuario.buscarPorEmail(user.getUsername());
			despesas = repository.findAll(usuario);
		}
		return despesas;
	}

	@Override
	public PaginacaoUtil<Despesa> buscarPorPagina(int pagina, String direcao) {
		return repository.buscaPaginada(pagina, direcao);
	}

	@Override
	public long buscarPorDescricao(String descricao) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = serviceUsuario.buscarPorEmail(user.getUsername());
		return repository.findByDescricao(descricao, usuario);
	}

	@Override
	public boolean temParcelaQuitada(Long id) {
		Despesa despesa = buscarPorId(id);
		for (ControleDespesa controleDespesa : despesa.getControleDespesas()) {
			if (controleDespesa.getStatus().equals("QUITADO")) {
				return true;
			}
		}
		return false;
	}

}
