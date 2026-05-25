export type TreeListItem = {
  id: number
  name: string
  categoryName: string
}

export type JsTreeNode = {
  id: string
  parent: string
  text: string
  icon?: string
  state?: { opened?: boolean }
}

/** 레거시 jstree_CvtVPath2TreeData */
export function buildCategoryTreeData(list: TreeListItem[]): JsTreeNode[] {
  let newParentId = 1
  const listOut: JsTreeNode[] = [{ id: 'root', parent: '#', text: 'Root', state: { opened: true } }]

  for (const item of list) {
    const parts = [...item.categoryName.split('/').filter(Boolean), item.name]
    let parent = 'root'
    for (let j = 0; j < parts.length; j++) {
      const segment = parts[j]
      let existing: JsTreeNode | undefined
      for (const node of listOut) {
        if (node.text === segment && node.parent === parent && node.id.startsWith('parent')) {
          existing = node
          break
        }
      }
      if (!existing) {
        if (j === parts.length - 1) {
          listOut.push({
            id: String(item.id),
            parent,
            text: segment,
            icon: 'glyphicon glyphicon-file',
          })
        } else {
          const folderId = `parent${newParentId}`
          listOut.push({ id: folderId, parent, text: segment })
          parent = folderId
          newParentId++
        }
      } else {
        parent = existing.id
      }
    }
  }
  return listOut
}

export function filterTreeList(
  list: TreeListItem[],
  keywords: string,
  extraFilter?: (item: TreeListItem) => boolean,
): TreeListItem[] {
  let filtered = list
  if (extraFilter) filtered = filtered.filter(extraFilter)
  if (!keywords.trim()) return filtered

  const trimmed = keywords.trim()
  if (!trimmed.includes(' ') && !trimmed.includes(':')) {
    const q = trimmed.toLowerCase()
    return filtered.filter((d) => d.name.toLowerCase().includes(q))
  }

  let dsName = ''
  let dsrName = ''
  let wgName = ''
  for (const part of trimmed.split(/\s+/)) {
    const w = part.trim()
    if (w.startsWith('ds:')) dsName = w.slice(3).toLowerCase()
    if (w.startsWith('dsr:')) dsrName = w.slice(4).toLowerCase()
    if (w.startsWith('wg:')) wgName = w.slice(3).toLowerCase()
  }
  return filtered.filter((d) => {
    const nameOk = !dsName || d.name.toLowerCase().includes(dsName)
    const wgOk = !wgName || d.name.toLowerCase().includes(wgName)
    const dsrOk = !dsrName || (d as TreeListItem & { datasourceName?: string }).datasourceName?.toLowerCase().includes(dsrName)
    return nameOk && wgOk && (dsrOk ?? true)
  })
}
