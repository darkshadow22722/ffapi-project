from app import config
from app.main import app

if __name__ == '__main__':
    app.run(host=config.server_host, port=config.server_port)
