import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testtask.R

class FeaturedCollectionsAdapter(private val collectionTitles: List<String>) :
    RecyclerView.Adapter<FeaturedCollectionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_featured_collection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val title = collectionTitles[position]
        holder.bind(title)
    }

    override fun getItemCount(): Int {
        return collectionTitles.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)

        fun bind(title: String) {
            titleTextView.text = title
        }
    }
}