from flask import Blueprint, request, jsonify, session

api = Blueprint('api', 'api', url_prefix='/api')

@api.route('/version')
def api_version():
    return jsonify({'version': 1})