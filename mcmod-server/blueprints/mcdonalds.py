import json
import os
from _sha256 import sha256
from base64 import b64encode

import requests
from ffapi.mcd import utils as mcdutils
from flask import Blueprint, request, Response, abort, send_from_directory

from app import utils, offers, config
from blueprints.token_factory import token

mcdonalds = Blueprint('mcdonalds', __name__)


@mcdonalds.route('/prd-con/v3/consumers/me/verificationtoken')
def verification_token():
    return '{"verificationToken":"123456","expiryDate":"2030-01-01T00:00:00Z"}'


@mcdonalds.route('/prd-cfg/v3/configurations')
def configurations():
    headers = {'X-Dif-Platform': 'android'}

    r = requests.get('https://dif-dot-prd-euw-gmal-mcdonalds.appspot.com/plexure/v1/cfg/v3/configurations',
                     headers=headers)

    if r.status_code != 200:
        return utils.request_error(r)

    x = json.loads(r.content.decode())

    if config.override_assets:
        server_url = config.server_external_url

        x['activityApiUrl'] = '{}/act/v3'.format(server_url)
        x['advertisementApiUrl'] = '{}/adv/v3'.format(server_url)
        x['advertisementImagePrefix'] = '{}/images'.format(server_url)
        x['assetsDownloadPrefix'] = '{}/images'.format(server_url)
        x['authorizationApiUrl'] = '{}/auth'.format(server_url)
        x['categoryImagePrefix'] = '{}/images'.format(server_url)
        x['configurationApiUrl'] = '{}/cfg/v3'.format(server_url)
        x['consumerApiUrl'] = '{}/con/v3'.format(server_url)
        x['locationApiUrl'] = '{}/loc/v3'.format(server_url)
        x['offerApiUrl'] = '{}/off/v3'.format(server_url)
        x['offerImagePrefix'] = '{}/images'.format(server_url)
        x['redeemedOfferImagePrefix'] = '{}/images'.format(server_url)

    return utils.json_response(x)


@mcdonalds.route('/prd-con/v3/logins', methods=['POST'])
@mcdonalds.route('/prd-con/v3/emailRegistrations', methods=['POST'])
@mcdonalds.route('/prd-con/v3/DeviceRegistration', methods=['POST'])
def device_registration():
    x = {
        'access_token': 'from_hexile_with_love',
        'token_type': 'ffapi_project',
        'consumerInfo': {
            'firstName': 'McMod',
            'emailAddress': 'McMod'
        },
        'crossReferences': None,
        'jwtAccessToken': None,
        'jwtRefreshToken': None
    }

    return utils.json_response(x)


def get_authorization():
    android_id = mcdutils.get_random_android_id()
    username = mcdutils.generate_username(android_id)
    password = mcdutils.generate_password(android_id)
    vmob = mcdutils.generate_vmob_uid(android_id)

    data = {
        'username': username,
        'password': password,
        'grant_type': 'password'
    }
    data_json = json.dumps(data)
    digest = b64encode(sha256(data_json.encode()).digest()).decode()

    headers = mcdutils.get_headers(vmob)
    headers['Digest'] = 'SHA-256=' + digest

    r = requests.post('https://dif-dot-prd-euw-gmal-mcdonalds.appspot.com/plexure/v1/con/v3/DeviceRegistration',
                      data=data_json, headers=headers)

    if r.status_code != 200:
        print(r.status_code)
        print(r.content)
        return utils.request_error(r)

    x = json.loads(r.content.decode())
    headers['Authorization'] = '{} {}'.format(x['token_type'], x['access_token'])

    return headers


@mcdonalds.route('/prd-adv/v3/advertisements')
def advertisements():
    if 'X-Vmob-Cost-Center' not in request.headers:
        abort(500)
    merchant_id = int(request.headers['X-Vmob-Cost-Center'][-3:])

    URL = 'https://dif-dot-prd-euw-gmal-mcdonalds.appspot.com/plexure/v1/adv/v3/advertisements?offset=0&merchantId={}&ignoreDailyTimeFilter=false&limit=100&placement=CD'.format(merchant_id)

    headers = get_authorization()

    r = requests.get(URL, headers=headers)

    return utils.json_response(json.loads(r.content.decode()))


@mcdonalds.route('/prd-off/v3/loyaltycards')
def loyaltycards():
    return '[]'


@mcdonalds.route('/xy/<x>/<y>/')
def image(x, y):
    if request.args['path'].startswith('custom'):
        return send_from_directory(os.path.join('static', 'images'), filename=request.args['path'])
    else:
        # headers = session.headers.copy()
        # headers['If-Modified-Since'] = request.headers.get('If-Modified-Since', None)
        r = requests.get('https://cfg-west-europe-gma.azureedge.net' + request.path, params=request.args)
        return Response(response=r.content, headers=r.headers.items(), status=r.status_code)


@mcdonalds.route('/prd-off/v3/offers')
def v3_offers():
    if 'X-Vmob-Cost-Center' not in request.headers:
        abort(500)
    merchant_id = int(request.headers['X-Vmob-Cost-Center'][-3:])

    language = 'en'
    if 'Accept-Language' in request.headers:
        language = request.headers['Accept-Language'].split('-')[0]

    merchant_offers = offers.get_offers(merchant_id)
    for x in merchant_offers:
        x['image'] = 'op{}_{}-1.png'.format(x['id'], language)

    return utils.json_response(merchant_offers)


@mcdonalds.route('/prd-off/v3/offers/<offer>/termsAndConditions')
def terms_and_conditions(offer):
    headers = mcdutils.get_random_headers()

    r = requests.get('https://dif-dot-prd-euw-gmal-mcdonalds.appspot.com/plexure/v1/off/v3/offers/{}/termsAndConditions'
                     .format(offer), headers=headers)

    x = json.loads(r.content.decode())

    return utils.json_response(x)


@mcdonalds.route('/prd-con/v3/consumers/redeemedOffers', methods=['GET', 'POST'])
def redeem_offers():
    if request.method == 'POST':
        headers = get_authorization()

        body = request.data
        headers['Digest'] = request.headers['Digest']
        headers['Date'] = request.headers['Date']
        headers['X-Dif-Authorization'] = 'Token headers="x-vmob-device_type",token=' + token

        r = requests.request('POST',
                             'https://dif-dot-prd-euw-gmal-mcdonalds.appspot.com/plexure/v1/con/v3/consumers/redeemedOffers',
                             data=body, headers=headers)

        if r.status_code != 200:
            return utils.request_error(r)

        x = json.loads(r.content.decode())

        return x
    else:
        # TODO: implement offers
        abort(500)


@mcdonalds.route('/prd-con/v3/consumers', methods=['GET', 'PUT'])
@mcdonalds.route('/prd-con/v3/consumers/consent', methods=['GET', 'POST'])
def consent():
    if request.method == 'GET':
        return utils.json_response('{"firstName":"McMod","emailAddress":"mcmod@hexile.xyz"}', dump=False)
    else:
        if config.server_debug:
            print(request.data)

        return Response(status=201)


@mcdonalds.route('/prd-con/v3/consumers/me/tagvalues', methods=['GET', 'PUT'])
def tag_values():
    if request.method == 'GET':
        return utils.json_response('{"tagValueReferenceCodes":["McMod"]}', dump=False)
    else:
        if config.server_debug:
            print(request.data)

        return Response(status=202)


@mcdonalds.route('/prd-act/v3/activities', methods=['POST'])
@mcdonalds.route('/prd-con/v3/crossReferences', methods=['POST'])
def log_requests():
    if config.server_debug:
        print(request.data)

    return Response(status=201)
