from flask import Flask

# Blueprints
from api.api import api

app = Flask(__name__)
app.register_blueprint(api)


@app.route('/')
def hello_world():
    return 'Hello, World!'

if __name__ == "__main__":
    app.run()    