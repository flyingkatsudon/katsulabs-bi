import type { JsTreeNode } from './configTreeData'

const STORAGE_PREFIX = 'katsulabs-jstree-open:'

function storageKey(treeId: string) {
  return `${STORAGE_PREFIX}${treeId}`
}

export function folderNodeIds(nodes: JsTreeNode[]): string[] {
  return nodes.filter((n) => n.id === 'root' || n.id.startsWith('parent')).map((n) => n.id)
}

export function readJsTreeOpenState(treeId: string): Set<string> {
  try {
    const raw = sessionStorage.getItem(storageKey(treeId))
    if (raw) {
      const ids = JSON.parse(raw) as string[]
      return new Set(ids.length > 0 ? ids : ['root'])
    }
  } catch {
    /* ignore */
  }
  return new Set(['root'])
}

export function writeJsTreeOpenState(treeId: string, ids: Iterable<string>) {
  sessionStorage.setItem(storageKey(treeId), JSON.stringify([...ids]))
}

export function applyJsTreeOpenState(nodes: JsTreeNode[], opened: Set<string>): JsTreeNode[] {
  return nodes.map((node) => ({
    ...node,
    state: {
      ...node.state,
      opened: opened.has(node.id),
    },
  }))
}

export function rememberJsTreeNodeOpened(treeId: string, nodeId: string) {
  const opened = readJsTreeOpenState(treeId)
  opened.add(nodeId)
  writeJsTreeOpenState(treeId, opened)
}

export function rememberJsTreeNodeClosed(treeId: string, nodeId: string) {
  const opened = readJsTreeOpenState(treeId)
  opened.delete(nodeId)
  writeJsTreeOpenState(treeId, opened)
}

export function rememberJsTreeOpenAll(treeId: string, nodes: JsTreeNode[]) {
  writeJsTreeOpenState(treeId, folderNodeIds(nodes))
}

export function rememberJsTreeCloseAll(treeId: string) {
  writeJsTreeOpenState(treeId, ['root'])
}
