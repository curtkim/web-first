import { of, Subject, BehaviorSubject, range, concat, interval } from 'rxjs';
import { buffer, delay, finalize, map, flatMap, concatMap, filter, tap, distinct } from 'rxjs/operators';



// 출발지, 도착지를 고정한다.
const ORIGIN = 0
const DEST = -100

function makeApproachArriveMessage([_, status, pos]) {  
  if( status == "Pickup"){
    const dist = Math.abs(pos - ORIGIN)
    if( dist <= 15)
      return "Origin Arrive"
    else if(dist <= 30)
      return "Origin Approach"
  }
  else if( status == "Drive"){
    const dist = Math.abs(pos - DEST)
    if(dist <= 15)
      return "Destination Arrive"
  }
  return ""
}

// [call_id, status, pos]
// call_id : 1~3
// status : Pickup -> Drive -> Wait
// pos : 100 -> 0 -> -100 -> 0 -> 100
const source$ = range(1, 3).pipe(
  flatMap(call_id =>{
    return range(1, 31.4*2).pipe(
      map(v => {
        const pos = Math.cos(v/10)*100
        if( v < 15.7)
          return [call_id, "Pickup", pos]
        else if( v < 31.4)
          return [call_id, "Drive", pos]
        else
          return [call_id, "Wait", pos]    
      })
    )    
  })  
)

source$.pipe(
  map(arr=> [...arr, makeApproachArriveMessage(arr)]),
  distinct(arr => arr[0]+arr[3]),
).subscribe(console.log)
