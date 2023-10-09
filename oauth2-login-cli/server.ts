import * as http from "http";
import { createServer, IncomingMessage, ServerResponse } from 'http';
var url = require('url');

//create a server object:
http
  .createServer(function (req: IncomingMessage, res: ServerResponse) {
		const queryData = url.parse(req.url, true).query;
		console.log(queryData.a);
    res.write("Hello World!"); //write a response to the client
    res.end(); //end the response
  })
  .listen(9000);
