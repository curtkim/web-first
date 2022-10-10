import { describe, expect, it } from "vitest";

describe('partial', ()=>{

  it("test", ()=>{
    interface Todo{
      title: string,
      description: string
    }

    function updateTodo(todo: Todo, fields: Partial<Todo>){
      return { ...todo, ...fields}
    }

    const todo1 = {
      title: 'organize desk',
      description: "clear"
    }
    const todo2 = updateTodo(todo1, {
      description: 'throw'
    })

    expect(todo2.description).toBe('throw')
  })
})