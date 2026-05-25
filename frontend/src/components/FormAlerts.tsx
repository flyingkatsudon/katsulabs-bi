type FormAlertsProps = {
  message: string | null
  error: string | null
}

export function FormAlerts({ message, error }: FormAlertsProps) {
  return (
    <>
      {message && <div className="alert alert-success">{message}</div>}
      {error && <div className="alert alert-danger">{error}</div>}
    </>
  )
}
