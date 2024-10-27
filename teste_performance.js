import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '30s', target: 50 }, // Simula 50 usuários durante 30 segundos
        { duration: '1m', target: 100 }, // Aumenta para 100 usuários durante 1 minuto
        { duration: '30s', target: 0 },   // Volta a 0 usuários
    ],
};

export default function () {
    // url abaixo para cenários onde o K6 e a API estão rodando pelo Docker
    const url = 'http://host.docker.internal:8081/api/votacao/resultado?pautaId=a7a01500-1fdc-4654-85dd-cec3d73d63f2';
    const res = http.get(url);

    // Verifica se a resposta está 200
    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1); // Espera 1 segundo entre as requisições
}

