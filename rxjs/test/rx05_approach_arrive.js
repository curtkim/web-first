import { interval, BehaviorSubject } from 'rxjs';
import { map, distinctUntilChanged, takeWhile } from 'rxjs/operators';


const config$ = new BehaviorSubject(1000)

const gpsOrigin$ = interval(50)

const distance$ = gpsOrigin$.pipe(
  map(it=> 100-it),
  takeWhile(it => it > 0)
)

function mystring(dist) {
  if( dist > 50)
    return "far"
  else if ( dist > 10)
    return "approach"
  else
    return "arrive"
}

const result$ = distance$.pipe(
  map(mystring),
  distinctUntilChanged(),
)

distance$.subscribe(console.log)
result$.subscribe(console.log)

/*
setTimeout(function(){
  config$.next(100)
}, 3000)

setTimeout(function(){
  config$.next(200)
}, 5000)
*/