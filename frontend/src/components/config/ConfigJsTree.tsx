import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { ensureJsTreeLoaded } from '../../legacy/loadJsTree'
import type { JsTreeNode } from '../../utils/configTreeData'
import {
  applyJsTreeOpenState,
  readJsTreeOpenState,
  rememberJsTreeCloseAll,
  rememberJsTreeNodeClosed,
  rememberJsTreeNodeOpened,
  rememberJsTreeOpenAll,
} from '../../utils/configJsTreeState'

type JQueryTree = {
  jstree: (cmd: string | boolean | Record<string, unknown>) => JQueryTree
  on: (event: string, handler: (e: unknown, data: { node: { id: string; children: string[] } }) => void) => void
  off: (event: string) => void
}

type JQueryFn = (sel: string) => JQueryTree

type JsTreeInstance = {
  deselect_node: (n: unknown) => void
  toggle_node: (n: unknown) => void
  deselect_all: () => void
  select_node: (id: string) => void
  open_all: () => void
  close_all: () => void
}

type ConfigJsTreeProps = {
  treeId: string
  treeData: JsTreeNode[]
  selectedId: string | null
  onSelectLeaf: (id: number) => void
  maxHeight?: string
  showExpandControls?: boolean
}

function getJQuery() {
  return (window as Window & { jQuery?: JQueryFn & { fn?: { jstree?: unknown } } }).jQuery
}

function getTreeInstance(treeId: string): JsTreeInstance | null {
  const jq = getJQuery()
  if (!jq) return null
  try {
    return jq(`#${treeId}`).jstree(true) as unknown as JsTreeInstance
  } catch {
    return null
  }
}

export function ConfigJsTree({
  treeId,
  treeData,
  selectedId,
  onSelectLeaf,
  maxHeight = '45vh',
  showExpandControls = true,
}: ConfigJsTreeProps) {
  const mounted = useRef(false)
  const syncingSelectionRef = useRef(false)
  const [jsTreeReady, setJsTreeReady] = useState(false)
  const [loadError, setLoadError] = useState<string | null>(null)

  const treeDataWithOpenState = useMemo(
    () => applyJsTreeOpenState(treeData, readJsTreeOpenState(treeId)),
    [treeData, treeId],
  )

  useEffect(() => {
    let cancelled = false
    setLoadError(null)
    void ensureJsTreeLoaded()
      .then(() => {
        if (!cancelled) {
          setJsTreeReady(true)
          setLoadError(null)
        }
      })
      .catch((e) => {
        if (!cancelled) {
          setJsTreeReady(false)
          setLoadError(e instanceof Error ? e.message : '트리 로드 실패')
        }
      })
    return () => {
      cancelled = true
    }
  }, [])

  useEffect(() => {
    if (!jsTreeReady) return
    const jq = getJQuery()
    if (!jq?.fn?.jstree) return

    const el = jq(`#${treeId}`)
    if (mounted.current) {
      try {
        el.jstree('destroy')
      } catch {
        /* ignore */
      }
    }

    el.jstree({
      core: {
        multiple: false,
        animation: true,
        data: treeDataWithOpenState,
        expand_selected_onload: true,
        check_callback: (operation: string, _n: unknown, node_parent: { id: string }) => {
          if (operation === 'move_node') {
            const pid = node_parent.id
            return pid.startsWith('parent') || pid === 'root'
          }
          return true
        },
      },
      types: {
        default: { valid_children: ['default', 'file'] },
        file: { icon: 'glyphicon glyphicon-file' },
      },
      plugins: ['types', 'unique', 'sort'],
    })

    const onOpen = (_e: unknown, data: { node: { id: string } }) => {
      rememberJsTreeNodeOpened(treeId, data.node.id)
    }

    const onClose = (_e: unknown, data: { node: { id: string } }) => {
      rememberJsTreeNodeClosed(treeId, data.node.id)
    }

    const onSelect = (_e: unknown, data: { node: { id: string; children: string[] } }) => {
      if (syncingSelectionRef.current) return
      const node = data.node
      if (node.children.length > 0) {
        const tree = el.jstree(true) as unknown as JsTreeInstance
        tree.deselect_node(node)
        tree.toggle_node(node)
        return
      }
      if (node.id !== 'root' && !node.id.startsWith('parent')) {
        onSelectLeaf(Number(node.id))
      }
    }

    el.on('open_node.jstree', onOpen)
    el.on('close_node.jstree', onClose)
    el.on('select_node.jstree', onSelect)
    mounted.current = true

    return () => {
      el.off('open_node.jstree')
      el.off('close_node.jstree')
      el.off('select_node.jstree')
      try {
        el.jstree('destroy')
      } catch {
        /* ignore */
      }
      mounted.current = false
    }
  }, [jsTreeReady, treeDataWithOpenState, onSelectLeaf, treeId])

  useEffect(() => {
    if (!jsTreeReady) return
    const jq = getJQuery()
    if (!jq || !selectedId || selectedId === 'new') return
    if (!/^\d+$/.test(selectedId)) return
    if (!document.getElementById(treeId)) return
    try {
      const tree = getTreeInstance(treeId)
      if (!tree) return
      syncingSelectionRef.current = true
      tree.deselect_all()
      tree.select_node(selectedId)
      window.setTimeout(() => {
        syncingSelectionRef.current = false
      }, 0)
    } catch {
      /* not ready */
    }
  }, [jsTreeReady, selectedId, treeId])

  const handleOpenAll = useCallback(() => {
    const tree = getTreeInstance(treeId)
    if (!tree) return
    tree.open_all()
    rememberJsTreeOpenAll(treeId, treeData)
  }, [treeId, treeData])

  const handleCloseAll = useCallback(() => {
    const tree = getTreeInstance(treeId)
    if (!tree) return
    tree.close_all()
    rememberJsTreeCloseAll(treeId)
  }, [treeId])

  return (
    <div className="panel-body" style={{ padding: '10px 0', overflowX: 'auto', fontSize: 'small', maxHeight }}>
      {showExpandControls && jsTreeReady && !loadError && (
        <div style={{ margin: '0 10px 8px' }}>
          <button type="button" className="btn btn-default btn-xs" onClick={handleOpenAll}>
            일괄 열기
          </button>
          <button
            type="button"
            className="btn btn-default btn-xs"
            style={{ marginLeft: 4 }}
            onClick={handleCloseAll}
          >
            일괄 닫기
          </button>
        </div>
      )}
      {loadError && <p className="text-danger">{loadError}</p>}
      {!jsTreeReady && !loadError && <p className="text-muted">트리 로딩 중…</p>}
      <div id={treeId} />
    </div>
  )
}
