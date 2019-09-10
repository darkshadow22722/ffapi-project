import json
import os
import time
from base64 import b64decode
from json import JSONDecodeError

from flask import Blueprint, Response, abort, request

from app import config
from app.utils import b64pad

token_factory = Blueprint('token_factory', __name__)

token = None
token_file = 'token.txt'

if os.path.isfile(token_file):
    print('Loading "{}"...'.format(token_file))
    with open(token_file) as f:
        token = f.read()


@token_factory.route('/available', methods=['GET'])
def is_available():
    if token is None:
        response = {
            'response': 'I need a token.'
        }
        return Response(status=200, content_type='application/json', response=json.dumps(response))
    else:
        try:
            payload_b64 = token.split('.')[1]
            payload = json.loads(b64decode(b64pad(payload_b64.encode())).decode())
            current_timestamp = int(time.time())

            if current_timestamp - int(str(payload['timestampMs'])[:-3]) > 240:
                response = {
                    'response': 'I need a token.'
                }
                return Response(status=200, content_type='application/json', response=json.dumps(response))
            else:
                response = {
                    'response': 'I dont\' need a token.'
                }
                return Response(status=400, content_type='application/json', response=json.dumps(response))
        except (UnicodeDecodeError, JSONDecodeError):
            abort(500)


@token_factory.route('/token', methods=['GET', 'POST'])
def get_token():
    global token

    if request.method == 'GET':

        headers = request.headers

        # Check if authorization header
        if 'authorization' not in headers:
            error = {
                'error': 'Missing Authorization header.'
            }
            return Response(status=400, content_type='application/json', response=json.dumps(error))
        else:
            # Check right API key
            if headers['authorization'] not in config.token_factory_get_keys:
                error = {
                    'error': 'Wrong API key.'
                }
                return Response(status=401, content_type='application/json', response=json.dumps(error))
            else:
                if token is not None:
                    print('{} asked for token.'.format(b64decode(headers['authorization'].encode()).decode()))
                    return token
                else:
                    error = {
                        'error': 'Empty token.'
                    }
                    return Response(status=404, content_type='application/json', response=json.dumps(error))
    elif request.method == 'POST':
        data = request.data.decode()

        if data == '':
            error = {
                'error': 'Empty body.'
            }
            return Response(status=400, content_type='application/json', response=json.dumps(error))

        try:
            payload_b64 = data.split('.')[1]
            payload = json.loads(b64decode(b64pad(payload_b64.encode())).decode())

            if not payload['ctsProfileMatch'] or not payload['basicIntegrity']:
                error = {
                    'error': 'SafetyNet not passed.'
                }
                return Response(status=400, content_type='application/json', response=json.dumps(error))

            token = data
            with open('token.txt', 'w') as f:
                f.write(data)

            return Response(status=201, content_type='application/json', response='ok')
        except (UnicodeDecodeError, IndexError, JSONDecodeError):
            error = {
                'error': 'Wrong body.'
            }
            return Response(status=500, content_type='application/json', response=json.dumps(error))
