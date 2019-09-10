import json
import os
from functools import wraps

from flask import Response, request


def json_response(x, dump=True):
    if dump:
        return Response(response=json.dumps(x), status=200, content_type='application/json')
    else:
        return Response(response=x, status=200, content_type='application/json')


def error(message, code=403):
    x = {
        'error': message
    }
    return Response(status=code, content_type='application/json', response=json.dumps(x))


def request_error(r):
    return Response(response=r.content, headers=r.headers.items(), status=r.status_code)


def check_folder(path):
    if not os.path.isdir(path):
        os.mkdir(path)


def check_headers(f):
    @wraps(f)
    def wrapper(*args, **kwargs):
        necessary_headers = [
            'X-Vmob-Uid',
            'X-Vmob-Device-Type'
        ]

        if all(x in request.headers for x in necessary_headers):
            return f()
        else:
            return error('Missing necessary headers.')

    return wrapper


def b64pad(data):
    missing_padding = len(data) % 4
    if missing_padding:
        data += b'=' * (4 - missing_padding)

    return data
