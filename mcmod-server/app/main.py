from functools import wraps

from flask import Flask, redirect, request, send_from_directory

from app import config
from blueprints.init_apis import init_apis
from blueprints.mcdonalds import mcdonalds
from blueprints.token_factory import token_factory

# Main Flask app
app = Flask(__name__)
app.register_blueprint(token_factory)
app.register_blueprint(init_apis)
app.register_blueprint(mcdonalds)

# Add debug flag
if config.server_debug:
    app.debug = True


@app.before_request
def debug_headers():
    if config.server_debug:
        print(request.headers)


@app.after_request
def server_header(response):
    # Add custom server header
    response.headers['Server'] = '{}/{}'.format(config.server_name, config.server_version)
    return response


@app.route('/')
def index():
    return config.server_name + ' ' + config.server_version


@app.route('/author')
def author_redirect():
    return redirect('https://github.com/giacomoferretti', code=302)


@app.route('/donate')
def donate_redirect():
    return redirect('https://paypal.me/hexile0', code=302)


@app.route('/github')
def github_redirect():
    return redirect('https://github.com/giacomoferretti/ffapi-project/tree/master/mcmod-server', code=302)
