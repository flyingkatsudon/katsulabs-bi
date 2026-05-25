import type { ReactNode } from 'react'

type ConfigWorkbenchProps = {
  title: string
  icon: string
  sidebar: ReactNode
  children: ReactNode
  toolbar?: ReactNode
}

/** 레거시 config/*.html 과 동일한 col-md-3 / col-md-9 box-solid 레이아웃 */
export function ConfigWorkbench({ title, icon, sidebar, children, toolbar }: ConfigWorkbenchProps) {
  return (
    <div id="inner-container" className="content">
      <div className="row">
        <div className="col-md-3">
          <div className="box box-solid">
            <div className="box-header with-border">
              <i className={`fa ${icon}`} /> <h3 className="box-title"> {title}</h3>
              {toolbar && <div className="box-tools pull-right">{toolbar}</div>}
            </div>
            {sidebar}
          </div>
        </div>
        <div className="col-md-9">{children}</div>
      </div>
    </div>
  )
}
