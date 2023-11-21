import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testtask.R
import com.example.testtask.model.PexelsImageWrapper

class FeaturedCollectionsAdapter(private val imageList: List<PexelsImageWrapper>) :
    RecyclerView.Adapter<FeaturedCollectionsAdapter.ViewHolder>() {

    private val popularImageTitles: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_featured_collection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = imageList[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return imageList.take(7).size
    }

    fun updateTitles(titles: List<String>) {
        popularImageTitles.clear()
        popularImageTitles.addAll(titles)
        notifyDataSetChanged()
    }

    fun getPopularTitles(): List<String> {
        return popularImageTitles
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)

        fun bind(image: PexelsImageWrapper) {
            val title = image.photographer
            titleTextView.text = title
            title?.let { popularImageTitles.add(it) }
        }

    }
}