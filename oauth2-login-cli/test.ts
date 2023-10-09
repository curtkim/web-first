const querystring = require('node:querystring');
const spawn = require('child_process').spawn;

import * as http from "http";
import { createServer, IncomingMessage, ServerResponse } from 'http';

function waitCode(){
	const hostname = '127.0.0.1';
	const port = 9000;

	return new Promise((resolve, reject)=>{
		const server = http.createServer(function(req: IncomingMessage, res: ServerResponse) {
			console.log(`req.query=${req.url}`);
			const queryData = url.parse(req.url, true).query;
			server.close();
			resolve(queryData.code);

			res.statusCode = 200;
			res.setHeader('Content-Type', 'text/plain');
			res.end('Hello World');
		});
		 
		server.listen(port, hostname, () => {
			console.log(`Server running at http://${hostname}:${port}/`);
		});
	});
}


const result = require('dotenv').config()
if (result.error) {
  throw result.error
}

//console.log(result.parsed)
const uri = 'https://accounts.google.com/o/oauth2/v2/auth?'
const params = {
 scope: "https://www.googleapis.com/auth/drive.metadata.readonly",
 access_type: "offline",
 include_granted_scopes: true,
 response_type: "code",
 state:"state_parameter_passthrough_value",
 redirect_uri: result.parsed.REDIRECT_URI,
 client_id:result.parsed.CLIENT_ID,
}
console.log(uri+querystring.stringify(params));

const codePromise = waitCode();
// open default browser
spawn('open', [uri+querystring.stringify(params)]);
//console.log('running on http://localhost:9000');

codePromise.then((code)=>{
	console.log(code);
});

