import { test } from 'node:test';
import assert from 'node:assert/strict';
import { criarDemonstracao, lerEstado } from '../src/domain.ts';

test('dados salvos com versão incompatível são rejeitados', () => {
  const estado = { ...criarDemonstracao(), versao: 2 };
  const texto = JSON.stringify(estado);

  assert.throws(() => lerEstado(texto), /Versão dos dados locais incompatível/);
});

test('reconhecimento salvo com tipo inválido é rejeitado', () => {
  const estado = { ...criarDemonstracao(), reconhecidos: [123] };
  const texto = JSON.stringify(estado);

  assert.throws(() => lerEstado(texto), /Reconhecimentos locais inválidos/);
});

test('histórico salvo com mais de 120 leituras é rejeitado', () => {
  const estado = criarDemonstracao();
  const primeira = estado.series.chuva[0];
  estado.series.chuva = Array.from({ length: 121 }, () => ({ ...primeira }));
  const texto = JSON.stringify(estado);

  assert.throws(() => lerEstado(texto), /Histórico local inválido/);
});
