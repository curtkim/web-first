import type {PageServerLoad} from "./$types"

export const load: PageServerLoad = async (event) => {
  // 1.
  // console.log(event.locals)
  // return { getSession: [Funtion ] }

  // 2.
  // console.log(event)
  /*
  {
    cookies: {
      get: [Function: get],
      set: [Function: set],
      delete: [Function: delete],
      serialize: [Function: serialize]
    },
    fetch: [AsyncFunction (anonymous)],
      getClientAddress: [Function: getClientAddress],
    locals: { getSession: [Function (anonymous)] },
    params: {},
    platform: undefined,
    request: Request {
      [Symbol(realm)]: { settingsObject: [Object] },
      [Symbol(state)]: {
        method: 'GET',
          localURLsOnly: false,
          unsafeRequest: false,
          body: null,
          client: [Object],
          reservedClient: null,
          replacesClientId: '',
          window: 'client',
          keepalive: false,
          serviceWorkers: 'all',
          initiator: '',
          destination: '',
          priority: null,
          origin: 'client',
          policyContainer: 'client',
          referrer: 'client',
          referrerPolicy: '',
          mode: 'cors',
          useCORSPreflightFlag: false,
          credentials: 'same-origin',
          useCredentials: false,
          cache: 'default',
          redirect: 'follow',
          integrity: '',
          cryptoGraphicsNonceMetadata: '',
          parserMetadata: '',
          reloadNavigation: false,
          historyNavigation: false,
          userActivation: false,
          taintedOrigin: false,
          redirectCount: 0,
          responseTainting: 'basic',
          preventNoCacheCacheControlHeaderModification: false,
          done: false,
          timingAllowFailed: false,
          headersList: [HeadersList],
          urlList: [Array],
          url: [URL]
      },
      [Symbol(signal)]: AbortSignal { aborted: false },
      [Symbol(headers)]: HeadersList {
        [Symbol(headers map)]: [Map],
          [Symbol(headers map sorted)]: null
      }
    },
    route: { id: [Getter] },
    setHeaders: [Function: setHeaders],
    url: URL {
      href: 'http://localhost:5173/',
        origin: 'http://localhost:5173',
        protocol: 'http:',
        username: '',
        password: '',
        host: 'localhost:5173',
        hostname: 'localhost',
        port: '5173',
        pathname: '/',
        search: '',
        searchParams: URLSearchParams {},
      hash: ''
    },
    isDataRequest: false,
    depends: [Function: depends],
    parent: [AsyncFunction: parent]
  }
  */

  return {
    session: await event.locals.getSession(),
  }
}