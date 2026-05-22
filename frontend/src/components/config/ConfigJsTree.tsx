import { useEffect, useRef, useState } from 'react'
import { ensureJsTreeLoaded } from '../../legacy/loadJsTree'
import type { JsTreeNode } from '../../utils/configTreeData'

type JQueryTree = {
  jstree: (cmd: string | boolean | Record<string, unknown>) => JQueryTree
  on: (event: string, handler: (e: unknown, data: { node: { id: string; children: string[] } }) => void) => void
  off: (event: string) => void
}

type JQueryFn = (sel: string) => JQueryTree

type ConfigJsTreeProps = {
  treeId: string
  treeData: JsTreeNode[]
  selectedId: string | null
  onSelectLeaf: (id: number) => void
  maxHeight?: string
}

export function ConfigJsTree({
  treeId,
  treeData,
  selectedId,
  onSelectLeaf,
  maxHeight = '45vh',
}: ConfigJsTreeProps) {
  const mounted = useRef(false)
  const syncingSelectionRef = useRef(false)
  const [jsTreeReady, setJsTreeReady] = useState(false)
  const [loadError, setLoadError] = useState<string | null>(null)

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
    const jq = (window as Window & { jQuery?: JQueryFn & { fn?: { jstree?: unknown } } }).jQuery
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
        data: treeData,
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

    const onSelect = (_e: unknown, data: { node: { id: string; children: string[] } }) => {
      if (syncingSelectionRef.current) return
      const node = data.node
      if (node.children.length > 0) {
        const tree = el.jstree(true) as unknown as {
          deselect_node: (n: unknown) => void
          toggle_node: (n: unknown) => void
        }
        tree.deselect_node(node)
        tree.toggle_node(node)
        return
      }
      if (node.id !== 'root' && !node.id.startsWith('parent')) {
        onSelectLeaf(Number(node.id))
      }
    }

    el.on('select_node.jstree', onSelect)
    mounted.current = true

    return () => {
      el.off('select_node.jstree')
      try {
        el.jstree('destroy')
      } catch {
        /* ignore */
      }
      mounted.current = false
    }
  }, [jsTreeReady, treeData, onSelectLeaf, treeId])

  useEffect(() => {
    if (!jsTreeReady) return
    const $ = (window as Window & { jQuery?: JQueryFn }).jQuery
    if (!$ || !selectedId || selectedId === 'new') return
    if (!/^\d+$/.test(selectedId)) return
    const el = $(`#${treeId}`)
    if (!document.getElementById(treeId)) return
    try {
      const inst = el.jstree(true)
      if (!inst) return
      const tree = inst as unknown as {
        deselect_all: () => void
        select_node: (id: string) => void
      }
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

  return (
    <div className="panel-body" style={{ padding: '10px 0', overflowX: 'auto', fontSize: 'small', maxHeight }}>
      {loadError && <p className="text-danger">{loadError}</p>}
      {!jsTreeReady && !loadError && <p className="text-muted">트리 로딩 중…</p>}
      <div id={treeId} />
    </div>
  )
}
