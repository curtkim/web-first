import { assert, describe, expect, it } from 'vitest'
import {DateTime, Duration} from 'luxon'

describe('luxon', () => {
  it('constructor', () => {
    const dt = DateTime.local(2017, 5, 15, 8, 30);
    expect(dt.year).toBe(2017)
    expect(dt.toLocaleString()).toBe('5/15/2017')
    expect(dt.toISO()).toBe('2017-05-15T08:30:00.000+09:00')
  })

  it('math', () => {
    const dt = DateTime.local(2017, 5, 15, 8, 30);
    expect(dt.plus({hours: 1, minutes: 1})).toStrictEqual(
      DateTime.local(2017, 5, 15, 9, 31)
    )
  })

  it('duration', ()=>{
    const dt = DateTime.local(2017, 5, 15, 8, 30);
    const dur = Duration.fromObject({ hours: 2, minutes: 7 })

    expect(dt.plus(dur)).toStrictEqual(
      DateTime.local(2017, 5, 15, 10, 37)
    )
  })

  it('format by token', ()=>{
    const dt = DateTime.local(2017, 5, 15, 8, 30);
    expect(dt.toFormat('yyyy MM dd')).toBe("2017 05 15")
  })
})