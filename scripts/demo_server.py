"""Servidor de demonstração (so para prints): serve a tela e simula a API com dados ficticios. Uso: python scripts/demo_server.py -> http://localhost:8099"""
import json, os, time, uuid
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer

STATIC = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src", "main", "resources", "static")
now = int(time.time() * 1000)
H = 3600 * 1000


def pg(m, v, td=0, tp=0):
    return {"id": str(uuid.uuid4()), "metodo": m, "valor": v, "trocoDinheiro": td, "trocoPix": tp, "data": now}


def auto(placa, tipo, valor, horas, pagos=None, previsto=None):
    return {"id": str(uuid.uuid4()), "placa": placa, "tipo": tipo, "valor": valor, "valorPrevisto": previsto,
            "pago": bool(pagos), "entrada": now - int(horas * H), "pagamentos": pagos or [], "foto": None}


AUTOS = [
    auto("QWE-1A23", "carro", 20, 0.5),
    auto("RTY-4B56", "carro", 25, 3, [pg("dinheiro", 30, 0, 5)], 25),
    auto("UIO-7C89", "moto", 10, 2, [pg("pix", 10)]),
    auto("ASD-2D34", "carro", 30, 5, [pg("dinheiro", 10), pg("pix", 20)]),
    auto("FGH-5E67", "moto", 12, 1.2),
    auto("JKL-8F90", "carro", 20, 4, [pg("dinheiro", 20)]),
    auto("ZXC-3G45", "moto", 10, 0.3),
    auto("VBN-6H78", "carro", 25, 0.8),
]
BL = [{"id": "1", "placa": "XYZ-9K99", "motivo": "Saiu sem pagar", "foto": None, "criadoEm": now - 30 * H}]


class H_(SimpleHTTPRequestHandler):
    def __init__(self, *a, **k):
        super().__init__(*a, directory=STATIC, **k)

    def j(self, obj, code=200):
        b = json.dumps(obj).encode()
        self.send_response(code)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(b)))
        self.end_headers()
        self.wfile.write(b)

    def do_GET(self):
        p = self.path.split("?")[0]
        if p == "/Automoveis":
            return self.j({"automoveis": AUTOS, "total": len(AUTOS)})
        if p == "/Blacklist":
            return self.j(BL)
        if p == "/eventos":
            self.send_response(200)
            self.send_header("Content-Type", "text/event-stream")
            self.end_headers()
            try:
                while True:
                    self.wfile.write(b": ping\n\n")
                    self.wfile.flush()
                    time.sleep(15)
            except Exception:
                return
        return super().do_GET()

    def log_message(self, *a):
        pass


ThreadingHTTPServer(("127.0.0.1", 8099), H_).serve_forever()
