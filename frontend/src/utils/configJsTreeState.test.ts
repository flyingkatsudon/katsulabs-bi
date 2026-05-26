import { describe, expect, it, beforeEach } from 'vitest'
import {
  applyJsTreeOpenState,
  folderNodeIds,
  readJsTreeOpenState,
  rememberJsTreeCloseAll,
  rememberJsTreeNodeClosed,
  rememberJsTreeNodeOpened,
  rememberJsTreeOpenAll,
  writeJsTreeOpenState,
} from './configJsTreeState'
import type { JsTreeNode } from './configTreeData'

const nodes: JsTreeNode[] = [
  { id: 'root', parent: '#', text: 'Root' },
  { id: 'parent1', parent: 'root', text: 'Demo' },
  { id: '1', parent: 'parent1', text: 'widget-a' },
]

describe('configJsTreeState', () => {
  beforeEach(() => {
    sessionStorage.clear()
  })

  it('collects folder node ids', () => {
    expect(folderNodeIds(nodes)).toEqual(['root', 'parent1'])
  })

  it('applies opened state to nodes', () => {
    const opened = new Set(['root', 'parent1'])
    const out = applyJsTreeOpenState(nodes, opened)
    expect(out[0].state?.opened).toBe(true)
    expect(out[1].state?.opened).toBe(true)
    expect(out[2].state?.opened).toBe(false)
  })

  it('persists open and close actions', () => {
    rememberJsTreeNodeOpened('widgetTreeID', 'parent1')
    expect(readJsTreeOpenState('widgetTreeID').has('parent1')).toBe(true)
    rememberJsTreeNodeClosed('widgetTreeID', 'parent1')
    expect(readJsTreeOpenState('widgetTreeID').has('parent1')).toBe(false)
  })

  it('supports open all and close all', () => {
    rememberJsTreeOpenAll('widgetTreeID', nodes)
    expect(readJsTreeOpenState('widgetTreeID')).toEqual(new Set(['root', 'parent1']))
    rememberJsTreeCloseAll('widgetTreeID')
    expect(readJsTreeOpenState('widgetTreeID')).toEqual(new Set(['root']))
  })

  it('defaults to root open', () => {
    expect(readJsTreeOpenState('newTree')).toEqual(new Set(['root']))
  })

  it('round-trips through sessionStorage', () => {
    writeJsTreeOpenState('datasetTreeID', ['root', 'parent2'])
    expect(readJsTreeOpenState('datasetTreeID')).toEqual(new Set(['root', 'parent2']))
  })
})
