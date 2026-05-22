/** 국가·지역명 → [경도, 위도] (FoodMart sales_country 등 데모용) */
const GEO_LOOKUP: Record<string, [number, number]> = {
  usa: [-95.7, 37.1],
  'united states': [-95.7, 37.1],
  canada: [-106.3, 56.1],
  mexico: [-102.5, 23.6],
  uk: [-2.5, 54.5],
  'united kingdom': [-2.5, 54.5],
  france: [2.2, 46.2],
  germany: [10.4, 51.1],
  spain: [-3.7, 40.4],
  italy: [12.5, 41.9],
  brazil: [-51.9, -14.2],
  japan: [138.2, 36.2],
  india: [78.9, 21.9],
  australia: [133.8, -25.3],
  russia: [105.3, 61.5],
  korea: [127.8, 35.9],
  'south korea': [127.8, 35.9],
  'north america': [-100, 45],
  europe: [15, 50],
  asia: [100, 34],
  'pacific northwest': [-120, 47],
  california: [-119.4, 36.8],
  'central america': [-90, 15],
  bermuda: [-64.8, 32.3],
  bahamas: [-77.4, 25],
}

function hashCoord(name: string): [number, number] {
  let h = 0
  for (let i = 0; i < name.length; i++) h = (h * 31 + name.charCodeAt(i)) | 0
  const lng = (h % 120) - 60
  const lat = ((h >> 8) % 80) - 40
  return [lng, lat]
}

export function resolveGeoCoord(name: string): [number, number] | null {
  const raw = name.trim()
  if (!raw) return null
  const lower = raw.toLowerCase()
  if (GEO_LOOKUP[lower]) return GEO_LOOKUP[lower]
  for (const [key, coord] of Object.entries(GEO_LOOKUP)) {
    if (lower.includes(key)) return coord
  }
  return hashCoord(raw)
}
